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
import cl.udelvd.refactor.stats_feature.data.remote.dto.EventsByEmotionsDTO
import cl.udelvd.refactor.stats_feature.data.remote.dto.IntervieweeGenreDTO
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartZoomType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
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
                    viewModel.statsState.collect { statsState ->

                        when {
                            statsState.isLoading -> {}
                            else -> statsState.stats?.let {

                                val generalData = it.general

                                binding.nInterviewee.text =
                                    "N° entrevistados: ${generalData.nInterviewees}"
                                binding.nEvents.text = "N° eventos: ${generalData.nEvents}"

                                setGenderChart(it.intervieweeByGenre)

                                setEmoticonEvents(it.eventsByEmotions)
                            }
                        }
                    }
                }
            }
        }
        viewModel.getStats("Bearer $token")

        return binding.root
    }

    private fun setEmoticonEvents(eventsByEmotions: EventsByEmotionsDTO) {

        val aaChartModelEmoticonEvents = AAChartModel()
            .chartType(AAChartType.Pie)
            .title("Emoticones por evento")
            .colorsTheme(arrayOf("#0c9674", "#7dffc0", "#d11b5f", "#facd32", "#ffffa0"))
            .dataLabelsEnabled(true)
            .tooltipEnabled(true)
            .series(
                arrayOf(
                    AASeriesElement()
                        .name("Valor")
                        .data(
                            arrayOf(
                                arrayOf("Felicidad", eventsByEmotions.happy),
                                arrayOf("Tristeza", eventsByEmotions.sad),
                                arrayOf("Miedo", eventsByEmotions.fear),
                                arrayOf("Enojo", eventsByEmotions.angry)
                            )
                        )
                )
            )

        binding.emoticonEventsPieChart.aa_drawChartWithChartModel(aaChartModelEmoticonEvents)

    }

    private fun setGenderChart(intervieweeByGenre: IntervieweeGenreDTO) {

        val aaChartModelGender = AAChartModel()
            .chartType(AAChartType.Pie)
            .title("Pie chart por genero")
            .colorsTheme(arrayOf("#0c9674", "#7dffc0", "#d11b5f", "#facd32", "#ffffa0"))
            .dataLabelsEnabled(true)
            .tooltipEnabled(true)
            .zoomType(AAChartZoomType.XY)
            .legendEnabled(true)
            .series(
                arrayOf(
                    AASeriesElement()
                        .name("Valor")
                        .data(
                            arrayOf(
                                arrayOf("Hombres", intervieweeByGenre.totalMen),
                                arrayOf("Mujer", intervieweeByGenre.totalWomen),
                                arrayOf("Otro", intervieweeByGenre.totalOther)
                            )
                        )
                )
            )

        binding.genderPieChart.aa_drawChartWithChartModel(aaChartModelGender)
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