package net.inqer.touringapp.ui.dialogs

import android.content.DialogInterface
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import net.inqer.touringapp.R
import net.inqer.touringapp.data.models.Destination
import net.inqer.touringapp.data.models.DestinationPhoto
import net.inqer.touringapp.databinding.DialogDestinationDetailsBinding
import net.inqer.touringapp.util.DrawableHelpers
import net.inqer.touringapp.util.MarginItemDecoration

class DestinationBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: DialogDestinationDetailsBinding

    lateinit var onClose: () -> Unit

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

            val photos = it.getParcelableArrayList<DestinationPhoto>(PHOTOS)
            photos?.let { parcelables ->
                val list = parcelables.filterIsInstance<DestinationPhoto>()

                initRecyclerView(list)
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (::onClose.isInitialized) onClose()
    }

    private fun initRecyclerView(list: List<DestinationPhoto>) {
        val context = binding.root.context

        val adapter = DestinationPhotoAdapter { photo, _ ->
            DrawableHelpers.showPhotoDialog(context, photo.url).also {
                it.window?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
        }

        binding.photosRecycler.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.photosRecycler.addItemDecoration(
            MarginItemDecoration(
                32,
                MarginItemDecoration.Direction.HORIZONTAL
            )
        )
        binding.photosRecycler.setHasFixedSize(true)
        binding.photosRecycler.adapter = adapter


        adapter.submitList(list)
    }

    companion object {
        private const val TAG = "DestinationBottomSheet"
        private const val TITLE = "TITLE"
        private const val DESCRIPTION = "DESCRIPTION"
        private const val PHOTOS = "PHOTOS"

        fun newInstance(destination: Destination, onDismiss: () -> Unit): DestinationBottomSheet =
            DestinationBottomSheet().apply {
                val bundle = Bundle().apply {
                    putString(TITLE, destination.title)
                    putString(DESCRIPTION, destination.description)
                    putParcelableArrayList(PHOTOS, ArrayList(destination.destinationPhotos))
                }
                this.arguments = bundle
                this.onClose = onDismiss
            }
    }
}
