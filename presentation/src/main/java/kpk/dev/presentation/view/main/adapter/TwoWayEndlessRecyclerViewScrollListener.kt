package kpk.dev.presentation.view.main.adapter

import androidx.recyclerview.widget.RecyclerView

abstract class TwoWayEndlessRecyclerViewScrollListener constructor(recyclerView: RecyclerView): RecyclerView.OnScrollListener() {

    private var previousTotal = 0 //The total number of items in the dataset after the previous load
    private var loading = true // Set to true of still loading new data
    private val visibleThreshold = 5 // The minimum items below/above the scroll line when we should initiate a new load
    var firstVisibleItem: Int = 0
    var visibleItemCount:Int = 0
    var totalItemCount:Int = 0

    var mRecyclerViewHelper: RecyclerViewPositionHelper = RecyclerViewPositionHelper(recyclerView)

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        visibleItemCount = recyclerView.childCount
        totalItemCount = mRecyclerViewHelper.itemCount
        firstVisibleItem = mRecyclerViewHelper.findFirstVisibleItemPosition()

        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false
                previousTotal = totalItemCount
            }
        }
        if (!loading && (totalItemCount - visibleItemCount)
                <= (firstVisibleItem + visibleThreshold)) {

            onLoadNext()

            loading = true
        }
        if (!loading && (firstVisibleItem - visibleThreshold <= 0)) {
            onLoadPrevious()
            loading = true
        }
    }

    abstract fun onLoadNext()
    abstract fun onLoadPrevious()
}