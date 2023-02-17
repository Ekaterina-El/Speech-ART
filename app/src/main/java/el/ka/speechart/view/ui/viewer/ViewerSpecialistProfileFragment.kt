package el.ka.speechart.view.ui.viewer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import el.ka.speechart.R
import el.ka.speechart.databinding.ViewerSpecialistProfileFragmentBinding
import el.ka.speechart.other.Work
import el.ka.speechart.service.model.Review
import el.ka.speechart.view.adapter.list.reviews.ReviewsAdapter
import el.ka.speechart.view.ui.BaseFragment
import el.ka.speechart.viewModel.ViewerSpecialistProfileViewModel

class ViewerSpecialistProfileFragment(private val onGoBack: () -> Unit): BaseFragment() {
  private lateinit var binding: ViewerSpecialistProfileFragmentBinding
  private val viewerSpecialistProfileViewModel by activityViewModels<ViewerSpecialistProfileViewModel>()

  private lateinit var reviewsAdapter: ReviewsAdapter

  private val reviewsObserver = Observer<List<Review>> {
    reviewsAdapter.setItems(it)
  }

  val list = listOf(Work.LOAD_USER, Work.LOAD_REVIEWS)
  override val workObserver = Observer<List<Work>> {
    val isLoad =
      when {
        it.isEmpty() -> false
        else -> it.map { item -> if (list.contains(item)) 1 else 0 }.reduce { a, b -> a + b } > 0
      }
    binding.swipeRefreshLayout.isRefreshing = isLoad
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = ViewerSpecialistProfileFragmentBinding.inflate(layoutInflater, container, false)
    reviewsAdapter = ReviewsAdapter()

    binding.apply {
      reviewsAdapter = this@ViewerSpecialistProfileFragment.reviewsAdapter
      lifecycleOwner = viewLifecycleOwner
      master = this@ViewerSpecialistProfileFragment
      viewModel = this@ViewerSpecialistProfileFragment.viewerSpecialistProfileViewModel
    }

    requireActivity().onBackPressedDispatcher.addCallback(this) {
      goBack()
    }
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val color = requireContext().getColor(R.color.loader_color)
    binding.swipeRefreshLayout.setColorSchemeColors(color)
    binding.swipeRefreshLayout.setOnRefreshListener { viewerSpecialistProfileViewModel.uploadProfile() }

    val decorator = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
    binding.recyclerViewReviews.addItemDecoration(decorator)

    binding.noFound.findViewById<TextView>(R.id.message).text =
      getString(R.string.reviews_no_found)
  }

  override fun onResume() {
    super.onResume()
    viewerSpecialistProfileViewModel.work.observe(viewLifecycleOwner, workObserver)
    viewerSpecialistProfileViewModel.reviews.observe(viewLifecycleOwner, reviewsObserver)
  }

  override fun onStop() {
    super.onStop()
    viewerSpecialistProfileViewModel.work.removeObserver(workObserver)
    viewerSpecialistProfileViewModel.reviews.removeObserver(reviewsObserver)
  }

  fun goBack() {
    onGoBack()
  }
}