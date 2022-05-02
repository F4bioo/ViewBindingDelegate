

import android.view.LayoutInflater
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ActivityViewBindingDelegate<T : ViewBinding>(
    private val bindingClass: Class<T>
) : ReadOnlyProperty<ComponentActivity, T>, DefaultLifecycleObserver {

    private var binding: T? = null

    override fun getValue(thisRef: ComponentActivity, property: KProperty<*>): T =
        thisRef.binding()

    override fun onDestroy(owner: LifecycleOwner) {
        binding = null
        super.onDestroy(owner)
    }

    @Suppress("UNCHECKED_CAST")
    private fun ComponentActivity.binding(): T {
        binding?.let { return it }

        val inflateMethod = bindingClass.getMethod("inflate", LayoutInflater::class.java)

        val invokeLayout = inflateMethod.invoke(null, layoutInflater) as T

        setContentView(invokeLayout.root)

        return invokeLayout.also { binding = it }
    }
}

inline fun <reified T : ViewBinding> AppCompatActivity.viewBinding() =
    ActivityViewBindingDelegate(T::class.java)