package cl.udelvd.refactor.stats_feature.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import cl.udelvd.R
import cl.udelvd.databinding.FragmentNewStatsBinding
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import kotlinx.coroutines.launch
import timber.log.Timber

class NewStatsFragment : Fragment() {

    private lateinit var viewModel: StatsViewModel

    private var _binding: FragmentNewStatsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewStatsBinding.inflate(inflater, container, false)


        viewModel = ViewModelProvider(
            requireActivity(),
            ViewModelFactory(requireActivity().application)
        )[StatsViewModel::class.java]

        val sharedPreferences = requireContext().getSharedPreferences(
            getString(R.string.SHARED_PREF_MASTER_KEY),
            Context.MODE_PRIVATE
        )
        val token = sharedPreferences.getString(
            getString(R.string.SHARED_PREF_TOKEN_LOGIN),
            ""
        )


        val aaChartModelGender = AAChartModel()
            .chartType(AAChartType.Pie)
            .title("Pie chart por genero")
            .colorsTheme(arrayOf("#0c9674", "#7dffc0", "#d11b5f", "#facd32", "#ffffa0"))
            .dataLabelsEnabled(true)
            .tooltipEnabled(true)

        /*
        val aaChartModelEmoticonEvents = AAChartModel()
            .chartType(AAChartType.Pie)
            .title("Emoticones por evento")
            .colorsTheme(arrayOf("#0c9674", "#7dffc0", "#d11b5f", "#facd32", "#ffffa0"))
            .dataLabelsEnabled(true)
            .tooltipEnabled(true)
            .series(
                arrayOf(
                    AASeriesElement()
                        .name("Emoticone por evento")
                        .data(
                            arrayOf(
                                arrayOf("Feliz", 56),
                                arrayOf("Tristeza", 120),
                                arrayOf("Miedo", 6),
                                arrayOf("Enojo", 23)
                            )
                        )
                )
            )*/

        binding.genderPieChart.aa_drawChartWithChartModel(aaChartModelGender)
        //binding.emoticonEventsPieChart.aa_drawChartWithChartModel(aaChartModelEmoticonEvents)

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {

                //Error state
                launch {
                    viewModel.errorState.collect {
                        Timber.d("Error stats: $it")
                    }
                }

                //Stats state
                launch {
                    viewModel.statsState.collect {

                        when {
                            it.isLoading -> {}
                            else -> {
                                val generalData = it.stats.first().attributes.general

                                binding.nInterviewee.text =
                                    "N° entrevistados: ${generalData.nInterviewees}"
                                binding.nEvents.text = "N° eventos: ${generalData.nEvents}"
                            }
                        }
                    }
                }
            }
        }
        viewModel.getStats("Bearer $token")

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = NewStatsFragment()
    }
}