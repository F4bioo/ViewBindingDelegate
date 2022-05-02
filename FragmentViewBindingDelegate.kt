

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class FragmentViewBindingDelegate<T : ViewBinding>(
    private val bindingClass: Class<T>
) : ReadOnlyProperty<Fragment, T>, DefaultLifecycleObserver {

    private var binding: T? = null

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T =
        thisRef.binding()

    override fun onDestroy(owner: LifecycleOwner) {
        binding = null
        owner.lifecycle.removeObserver(this)       
        super.onDestroy(owner)
    }

    @Suppress("UNCHECKED_CAST")
    private fun Fragment.binding(): T {
        binding?.let { return it }

        viewLifecycleOwner.lifecycle.addObserver(this@FragmentViewBindingDelegate)

        val inflateMethod = bindingClass.getMethod("bind", View::class.java)

        val invokeLayout = inflateMethod.invoke(null, requireView()) as T

        return invokeLayout.also { binding = it }
    }
}

inline fun <reified T : ViewBinding> Fragment.viewBinding() =
    FragmentViewBindingDelegate(T::class.java)
