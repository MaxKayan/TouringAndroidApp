package net.inqer.touringapp.ui.dialogs

import android.content.DialogInterface
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import net.inqer.touringapp.R
import net.inqer.touringapp.data.models.Destination
import net.inqer.touringapp.databinding.DialogDestinationDetailsBinding

class DestinationBottomSheet(
    private val onDismiss: () -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var binding: DialogDestinationDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogDestinationDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheetInternal =
                d.findViewById<View>(R.id.design_bottom_sheet)
            bottomSheetInternal?.minimumHeight =
                Resources.getSystem().displayMetrics.heightPixels
        }

        arguments?.let {
            binding.title.text = it.getString(TITLE)
            binding.description.text = it.getString(DESCRIPTION)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismiss()
    }

    companion object {
        private const val TAG = "DestinationBottomSheet"
        private const val TITLE = "TITLE"
        private const val DESCRIPTION = "DESCRIPTION"

        fun newInstance(destination: Destination, onClose: () -> Unit): DestinationBottomSheet =
            DestinationBottomSheet(onClose).apply {
                val bundle = Bundle().apply {
                    putString(TITLE, destination.title)
                    putString(DESCRIPTION, destination.description)
                }
                this.arguments = bundle
                this.onSaveInstanceState(bundle)
            }
    }
}
