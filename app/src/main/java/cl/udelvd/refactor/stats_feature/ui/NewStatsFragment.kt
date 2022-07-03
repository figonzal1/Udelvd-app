package cl.udelvd.refactor.stats_feature.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import cl.udelvd.R
import cl.udelvd.databinding.FragmentNewStatsBinding
import cl.udelvd.models.Emoticon
import cl.udelvd.refactor.stats_feature.data.remote.dto.EventsByEmotionsDTO
import cl.udelvd.refactor.stats_feature.data.remote.dto.IntervieweeGenreDTO
import cl.udelvd.viewmodels.NewEventViewModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartZoomType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.launch
import timber.log.Timber

class NewStatsFragment : Fragment() {


    private var isRefresh: Boolean = false
    private lateinit var viewModel: StatsViewModel
    private var newEventViewModel: NewEventViewModel? = null

    private var _binding: FragmentNewStatsBinding? = null
    private val binding get() = _binding!!

    private var token: String? = null

    //EMOTICONES
    private var idSelectedEmoticon: Int = -1
    private var emoticonsList: MutableList<Emoticon> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewStatsBinding.inflate(inflater, container, false)


        viewModel = ViewModelProvider(
            requireActivity(),
            ViewModelFactory(requireActivity().application)
        )[StatsViewModel::class.java]

        newEventViewModel = ViewModelProvider(this)[NewEventViewModel::class.java]

        newEventViewModel!!.loadEmoticons().observe(viewLifecycleOwner) { emoticons ->

            if (emoticons != null && emoticons.size > 0) {
                emoticonsList = emoticons

                Timber.d(
                    getString(R.string.TAG_VIEW_MODEL_EMOTICON),
                    getString(R.string.VIEW_MODEL_LISTA_ENTREVISTADO_MSG)
                )
            }

        }

        val sharedPreferences = requireContext().getSharedPreferences(
            getString(R.string.SHARED_PREF_MASTER_KEY),
            Context.MODE_PRIVATE
        )
        token = sharedPreferences.getString(
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

                                isRefresh = true
                            }
                        }
                    }
                }
            }
        }

        configFilterBottomSheet()

        return binding.root
    }

    private fun setEmoticonEvents(eventsByEmotions: EventsByEmotionsDTO) {

        with(binding) {
            when {
                isRefresh -> {
                    emoticonEventsPieChart.aa_onlyRefreshTheChartDataWithChartOptionsSeriesArray(
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
                        ),
                        true
                    )
                }
                else -> {
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
                    emoticonEventsPieChart.aa_drawChartWithChartModel(aaChartModelEmoticonEvents)
                }
            }
        }
    }

    private fun setGenderChart(intervieweeByGenre: IntervieweeGenreDTO) {

        with(binding) {
            when {
                isRefresh -> {
                    genderPieChart.aa_onlyRefreshTheChartDataWithChartOptionsSeriesArray(
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
                        ),
                        true
                    )
                }
                else -> {
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
                    genderPieChart.aa_drawChartWithChartModel(aaChartModelGender)
                }
            }
        }
    }

    private fun configFilterBottomSheet() {

        setSpinnerGenre()

        val behavior = BottomSheetBehavior.from(binding.include.filterBottomSheet)

        behavior.isHideable = false

        with(binding.include) {


            tvName.text = "Felipe González"

            emoticonRadioGroup.setOnCheckedChangeListener { _, checkedId ->
                idSelectedEmoticon = findEmoticonId(checkedId)
            }

            btnFilter.setOnClickListener {

                val genreLetter = when {
                    etIntervieweeGenre.text.toString() == getString(R.string.SEXO_MASCULINO) -> "m"
                    etIntervieweeGenre.text.toString() == getString(R.string.SEXO_FEMENINO) -> "f"
                    etIntervieweeGenre.text.toString() == getString(R.string.SEXO_OTRO) -> "o"
                    else -> ""
                }

                viewModel.getStats("Bearer $token", idSelectedEmoticon, genreLetter)

                Toast.makeText(requireContext(), "Filter btn clicked", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }

    /**
     * Configuration for emoticon radioGroup
     */
    private fun findEmoticonId(checkedId: Int): Int {
        return when (checkedId) {
            R.id.radio_happy -> emoticonsList.find { it.description!!.contains(getString(R.string.happiness)) }!!.id
            R.id.radio_angry -> emoticonsList.find { it.description!!.contains(getString(R.string.anger)) }!!.id
            R.id.radio_fear -> emoticonsList.find { it.description!!.contains(getString(R.string.fear)) }!!.id
            R.id.radio_sad -> emoticonsList.find { it.description!!.contains(getString(R.string.sadness)) }!!.id
            else -> -1
        }
    }

    /**
     * Configuration for genre spinner
     */
    private fun setSpinnerGenre() {
        val genreArray = arrayOf(
            "All",
            getString(R.string.SEXO_MASCULINO),
            getString(R.string.SEXO_FEMENINO),
            getString(R.string.SEXO_OTRO)
        )
        val adapterSexo =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, genreArray)
        binding.include.etIntervieweeGenre.setAdapter<ArrayAdapter<String>>(adapterSexo)
        binding.include.etIntervieweeGenre.setText(genreArray[0], false)

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