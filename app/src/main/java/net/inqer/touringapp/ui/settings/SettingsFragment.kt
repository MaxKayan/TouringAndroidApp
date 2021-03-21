package net.inqer.touringapp.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import net.inqer.touringapp.databinding.FragmentSettingsBinding
import net.inqer.touringapp.ui.home.HomeViewModel

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var binding: FragmentSettingsBinding;

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSave.setOnClickListener {
            Snackbar.make(it, "Click", Snackbar.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val TAG = "SettingsFragment"
    }
}