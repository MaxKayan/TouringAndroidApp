package net.inqer.touringapp.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import net.inqer.touringapp.databinding.FragmentHomeBinding
import net.inqer.touringapp.util.Resource

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(TAG, "onViewCreated: CALLED")
        lifecycleScope.launchWhenCreated {
            Log.w(TAG, "onViewCreated: CREATED, FETCHING!")
            viewModel.fetchRoutesBrief()
        }

        lifecycleScope.launchWhenStarted {
            viewModel.routes.collect { event ->
                when (event) {
                    is Resource.Success -> {
                        Log.d(TAG, "onViewCreated: ${event.data}")
                        event.data?.forEach { Toast.makeText(context, it.title, Toast.LENGTH_LONG).show() }
                    }

                    is Resource.Error -> {
                        Log.e(TAG, "onViewCreated: $event")
                        Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                    }

                    is Resource.Loading -> {
//                        Snackbar.make(view, "Загрузка туров...", Snackbar.LENGTH_SHORT).show()
                    }

                    is Resource.Empty -> {
                        Log.d(TAG, "onViewCreated: empty routes")
                    }
                }
            }
        }
    }

    companion object {
        private const val TAG = "HomeFragment"
    }
}