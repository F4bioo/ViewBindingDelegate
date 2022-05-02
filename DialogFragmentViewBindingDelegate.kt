

import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class DialogFragmentViewBindingDelegate<T : ViewBinding>(
    private val bindingClass: Class<T>
) : ReadOnlyProperty<DialogFragment, T>, DefaultLifecycleObserver {

    private var binding: T? = null

    override fun getValue(thisRef: DialogFragment, property: KProperty<*>): T =
        thisRef.binding()

    override fun onDestroy(owner: LifecycleOwner) {
        binding = null
        super.onDestroy(owner)
    }

    @Suppress("UNCHECKED_CAST")
    private fun DialogFragment.binding(): T {
        binding?.let { return it }

        val inflateMethod = bindingClass.getMethod("inflate", LayoutInflater::class.java)

        val invokeLayout = inflateMethod.invoke(null, LayoutInflater.from(context)) as T

        return invokeLayout.also { binding = it }
    }
}

inline fun <reified T : ViewBinding> DialogFragment.viewBinding() =
    DialogFragmentViewBindingDelegate(T::class.java)
