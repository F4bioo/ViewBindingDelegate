

import android.view.LayoutInflater
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class BottomSheetViewBindingDelegate<T : ViewBinding>(
    private val bindingClass: Class<T>
) : ReadOnlyProperty<BottomSheetDialogFragment, T>, DefaultLifecycleObserver {

    private var binding: T? = null

    override fun getValue(thisRef: BottomSheetDialogFragment, property: KProperty<*>): T =
        thisRef.binding()

    override fun onDestroy(owner: LifecycleOwner) {
        binding = null
        super.onDestroy(owner)
    }

    @Suppress("UNCHECKED_CAST")
    private fun BottomSheetDialogFragment.binding(): T {
        binding?.let { return it }

        val inflateMethod = bindingClass.getMethod("inflate", LayoutInflater::class.java)

        val invokeLayout = inflateMethod.invoke(null, LayoutInflater.from(context)) as T

        return invokeLayout.also { binding = it }
    }
}

inline fun <reified T : ViewBinding> BottomSheetDialogFragment.viewBinding() =
    BottomSheetViewBindingDelegate(T::class.java)
