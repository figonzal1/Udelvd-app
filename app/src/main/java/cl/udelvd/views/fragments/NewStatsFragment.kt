package cl.udelvd.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cl.udelvd.databinding.FragmentNewStatsBinding
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement

class NewStatsFragment : Fragment() {

    private var _binding: FragmentNewStatsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewStatsBinding.inflate(inflater, container, false)

        val aaChartModelGender = AAChartModel()
            .chartType(AAChartType.Pie)
            .title("Pie chart por genero")
            .colorsTheme(arrayOf("#0c9674", "#7dffc0", "#d11b5f", "#facd32", "#ffffa0"))
            .dataLabelsEnabled(true)
            .tooltipEnabled(true)
            .series(
                arrayOf(
                    AASeriesElement()
                        .name("Genero por entrevistados")
                        .data(
                            arrayOf(
                                arrayOf("Mujeres", 56),
                                arrayOf("Hombres", 120)
                            )
                        )
                )
            )

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
            )

        binding.genderPieChart.aa_drawChartWithChartModel(aaChartModelGender)
        binding.emoticonEventsPieChart.aa_drawChartWithChartModel(aaChartModelEmoticonEvents)

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