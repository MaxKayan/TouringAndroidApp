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
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import net.inqer.touringapp.data.models.TourRouteBrief
import net.inqer.touringapp.databinding.FragmentHomeBinding
import net.inqer.touringapp.util.Resource

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: ToursAdapter

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        binding.swipeLayout.setOnRefreshListener {
//            viewModel.fetchRoutesBrief()
        }

//        Log.d(TAG, "onViewCreated: CALLED")
//        lifecycleScope.launchWhenCreated {
//            Log.w(TAG, "onViewCreated: CREATED, FETCHING!")
//            viewModel.fetchRoutesBrief()
//        }

        lifecycleScope.launchWhenStarted {
            viewModel.routes.collect { event ->
                when (event) {
                    is Resource.Success -> {
                        Log.d(TAG, "onViewCreated: ${event.data}")
                        adapter.submitList(event.data)

                        binding.swipeLayout.isRefreshing = false
                    }

                    is Resource.Error -> {
                        Log.e(TAG, "onViewCreated: $event")
                        Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()

                        binding.swipeLayout.isRefreshing = false
                    }

                    is Resource.Loading -> {
                        Log.d(TAG, "onViewCreated: Loading...")
//                        Snackbar.make(view, "Загрузка туров...", Snackbar.LENGTH_SHORT).show()

                        binding.swipeLayout.isRefreshing = true
                    }

                    is Resource.Empty -> {
                        Log.d(TAG, "onViewCreated: empty routes")
                    }
                }
            }

//            viewModel.routesBriefFlow.collect { routes ->
//                Log.d(TAG, "onViewCreated: Fragment received data list! $routes")
//                adapter.submitList(routes)
//            }
        }
    }

    private fun setupRecyclerView() {
        adapter = ToursAdapter(object : ToursAdapter.Companion.TourViewHolder.OnTourViewInteraction {
            override fun click(item: TourRouteBrief) {
//                Toast.makeText(context, item.title, Toast.LENGTH_SHORT).show()
            }
        })

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = adapter
    }

    companion object {
        private const val TAG = "HomeFragment"
    }
}