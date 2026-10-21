
package com.codepath.lab6

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

private const val TAG = "CampgroundFragment"
private val CAMPGROUND_URL =
    "https://developer.nps.gov/api/v1/campgrounds?api_key=${BuildConfig.API_KEY}"

class CampgroundFragment : Fragment() {

    private val campgrounds = mutableListOf<Campground>()
    private lateinit var campgroundsRecyclerView: RecyclerView
    private lateinit var campgroundAdapter: CampgroundAdapter
    private var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(
            R.layout.fragment_campground,
            container,
            false
        )

        campgroundsRecyclerView =
            view.findViewById(R.id.campground_recycler_view)

        campgroundsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext())

        campgroundsRecyclerView.setHasFixedSize(true)

        campgroundAdapter =
            CampgroundAdapter(requireContext(), campgrounds)

        campgroundsRecyclerView.adapter = campgroundAdapter

        return view
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        if (campgrounds.isEmpty() && !isLoading) {
            fetchCampgrounds()
        }
    }

    private fun fetchCampgrounds() {
        isLoading = true

        val client = AsyncHttpClient()

        client.get(CAMPGROUND_URL, object : JsonHttpResponseHandler() {

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String?,
                throwable: Throwable?
            ) {
                isLoading = false
                Log.e(
                    TAG,
                    "Failed to fetch campgrounds: $statusCode",
                    throwable
                )
            }

            override fun onSuccess(
                statusCode: Int,
                headers: Headers,
                json: JSON
            ) {
                isLoading = false

                try {
                    val parsedJson = createJson().decodeFromString(
                        CampgroundResponse.serializer(),
                        json.jsonObject.toString()
                    )

                    parsedJson.data?.let { list ->
                        campgrounds.clear()
                        campgrounds.addAll(list)

                        if (view != null) {
                            campgroundAdapter.notifyDataSetChanged()
                        }
                    }

                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing campgrounds", e)
                }
            }
        })
    }

    override fun onDestroyView() {
        campgroundsRecyclerView.adapter = null
        super.onDestroyView()
    }

    companion object {
        fun newInstance(): CampgroundFragment {
            return CampgroundFragment()
        }
    }
}
