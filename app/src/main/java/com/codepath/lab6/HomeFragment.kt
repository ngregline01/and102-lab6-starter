package com.codepath.lab6

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

private const val HOME_TAG = "HomeFragment"

private val HOME_URL =
    "https://developer.nps.gov/api/v1/campgrounds?limit=10&api_key=${BuildConfig.API_KEY}"

class HomeFragment : Fragment() {

    private val featuredCampgrounds = mutableListOf<Campground>()

    private lateinit var homeRecyclerView: RecyclerView
    private lateinit var homeAdapter: CampgroundAdapter
    private lateinit var progressBar: ProgressBar

    private var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(
            R.layout.fragment_home,
            container,
            false
        )

        homeRecyclerView =
            view.findViewById(R.id.homeRecyclerView)

        progressBar =
            view.findViewById(R.id.homeProgressBar)

        homeRecyclerView.layoutManager =
            LinearLayoutManager(requireContext())

        homeAdapter = CampgroundAdapter(
            requireContext(),
            featuredCampgrounds
        )

        homeRecyclerView.adapter = homeAdapter

        return view
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        if (featuredCampgrounds.isEmpty() && !isLoading) {
            fetchFeaturedCampgrounds()
        }
    }

    private fun fetchFeaturedCampgrounds() {

        isLoading = true
        progressBar.visibility = View.VISIBLE

        val client = AsyncHttpClient()

        client.get(HOME_URL, object : JsonHttpResponseHandler() {

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String?,
                throwable: Throwable?
            ) {
                isLoading = false

                if (view != null) {
                    progressBar.visibility = View.GONE
                }

                Log.e(
                    HOME_TAG,
                    "Failed to fetch featured campgrounds: $statusCode",
                    throwable
                )
            }

            override fun onSuccess(
                statusCode: Int,
                headers: Headers,
                json: JSON
            ) {
                isLoading = false

                if (view != null) {
                    progressBar.visibility = View.GONE
                }

                try {
                    val parsedJson = createJson().decodeFromString(
                        CampgroundResponse.serializer(),
                        json.jsonObject.toString()
                    )

                    parsedJson.data?.let { list ->
                        featuredCampgrounds.clear()
                        featuredCampgrounds.addAll(list)

                        if (view != null) {
                            homeAdapter.notifyDataSetChanged()
                        }
                    }

                } catch (e: Exception) {
                    Log.e(
                        HOME_TAG,
                        "Error parsing featured campgrounds",
                        e
                    )
                }
            }
        })
    }

    override fun onDestroyView() {
        homeRecyclerView.adapter = null
        super.onDestroyView()
    }
}
