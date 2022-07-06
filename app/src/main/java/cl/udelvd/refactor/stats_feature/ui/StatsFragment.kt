package cl.udelvd.refactor.stats_feature.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import cl.udelvd.R
import cl.udelvd.databinding.FragmentNewStatsBinding
import cl.udelvd.models.Emoticon
import cl.udelvd.refactor.interviewee_feature.domain.model.Interviewee
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

class StatsFragment : Fragment() {

    private var isRefresh: Boolean = false
    private lateinit var statsViewModel: StatsViewModel
    private var emoticonViewModel: NewEventViewModel? = null

    private var _binding: FragmentNewStatsBinding? = null
    private val binding get() = _binding!!

    private var token: String? = null

    //EMOTICONES
    private var idSelectedEmoticon: Int = -1
    private var emoticonsList: MutableList<Emoticon> = arrayListOf()

    //Entrevistados
    private lateinit var selectedInterviewees: MutableList<Interviewee>
    private var listItems: Array<String> = arrayOf()
    private lateinit var checkedItems: BooleanArray

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewStatsBinding.inflate(inflater, container, false)

        initViewModels()

        val sharedPreferences = requireContext().getSharedPreferences(
            getString(R.string.SHARED_PREF_MASTER_KEY),
            Context.MODE_PRIVATE
        )
        token = sharedPreferences.getString(
            getString(R.string.SHARED_PREF_TOKEN_LOGIN),
            ""
        )

        processStatsData()

        configFilterBottomSheet()

        /*
        val aaChartModelEmoticonEvents = AAChartModel()
            .chartType(AAChartType.Bubble)
            .title("Emoticones por evento")
            .dataLabelsEnabled(true)
            .tooltipEnabled(true)
            .yAxisMax(23f)
            .yAxisMin(0f)
            .yAxisTitle("Hora")
            .series(
                arrayOf(
                    AASeriesElement()
                        .name("OnePLus")
                        .data(
                            arrayOf(
                                20, //Accion 1
                                23 //Accion 2
                            )
                        ),
                    AASeriesElement()
                        .name("Samsung")
                        .data(
                            arrayOf(2.3, 2, 4)
                        ),
                    AASeriesElement()
                        .name("Apple")
                        .data(
                            arrayOf(5, 5.8, 4, 1)
                        ),
                    AASeriesElement()
                        .name("Huawei")
                        .data(
                            arrayOf()
                        )
                )
            )

        val options = aaChartModelEmoticonEvents.aa_toAAOptions()

        val hora1 = LocalTime.of(15, 30)
        val hora2 = LocalTime.of(6, 23)

        options.yAxis?.apply {
            this.max = 24f
            this.min = 0f
            this.labels = AALabels().rotation(-90f)
            this.tickInterval(1)
        }

        options.xAxis?.apply {
            this.labels = AALabels().rotation(-90f)
            this.categories(
                arrayOf(
                    "Accion1",
                    "Accion2",
                    "Accion3",
                    "Accion4",
                    "Accion5",
                    "Accion6",
                    "Accion7",
                    "Accion8",
                    "Accion9",
                    "Accion10",
                    "Accion11"
                )
            )
        }
        //options.plotOptions?.series?.pointInterval(24 * 3600 * 1000 )
        binding.eventsChart.aa_drawChartWithChartOptions(options)*/

        return binding.root
    }

    private fun initViewModels() {
        statsViewModel = ViewModelProvider(
            requireActivity(),
            ViewModelFactory(requireActivity().application)
        )[StatsViewModel::class.java]

        emoticonViewModel = ViewModelProvider(this)[NewEventViewModel::class.java]

        //LOAD EMOTICONS
        emoticonViewModel!!.loadEmoticons().observe(viewLifecycleOwner) { emoticons ->

            if (emoticons != null && emoticons.size > 0) {
                emoticonsList = emoticons

                Timber.d(
                    getString(R.string.TAG_VIEW_MODEL_EMOTICON),
                    getString(R.string.VIEW_MODEL_LISTA_ENTREVISTADO_MSG)
                )
            }

        }
    }

    private fun processStatsData() {
        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {

                //Error state
                launch {
                    statsViewModel.errorState.collect {
                        Timber.d("Error stats: $it")
                    }
                }

                //Stats state
                launch {
                    statsViewModel.statsState.collect { statsState ->

                        when {
                            statsState.isLoading -> {}
                            else -> statsState.stats?.let {

                                it.basicInformation.apply {

                                    binding.nInterviewee.text = String.format(
                                        getString(R.string.n_entrevistados),
                                        nInterviewees
                                    )
                                    binding.nEvents.text =
                                        String.format(getString(R.string.n_eventos), nEvents)
                                }

                                setGenderChart(it.intervieweeByGenre)

                                setEmoticonEventsChart(it.eventsByEmotions)

                                isRefresh = true
                            }
                        }
                    }
                }

                //IntervieweeWithEvents state
                launch {
                    statsViewModel.intervieweeState.collect { state ->

                        when {
                            state.isLoading -> {
                                binding.include.tvIntervieweeFilter.isEnabled = false
                                binding.include.ivIntervieweeFilter.isEnabled = false
                            }
                            state.interviewee.isNotEmpty() -> {

                                listItems = state.interviewee.map {
                                    "${it.name} ${it.lastName}"
                                }.toTypedArray()
                                checkedItems = BooleanArray(state.interviewee.size)

                                with(binding.include) {
                                    tvIntervieweeFilter.isEnabled = true
                                    ivIntervieweeFilter.isEnabled = true

                                    ivIntervieweeFilter.setOnClickListener {
                                        configIntervieweeFilterDialog(
                                            state.interviewee,
                                            listItems.toList()
                                        )
                                    }
                                    tvIntervieweeFilter.setOnClickListener {
                                        configIntervieweeFilterDialog(
                                            state.interviewee,
                                            listItems.toList()
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        statsViewModel.getIntervieweeWithEvents("Bearer $token")
    }

    private fun configIntervieweeFilterDialog(
        intervieweeList: List<Interviewee>,
        selectedItems: List<String>
    ) {

        AlertDialog.Builder(requireActivity())
            .setTitle(getString(R.string.title_dialog_selected_interviewee))
            .setCancelable(false)
            .setMultiChoiceItems(
                listItems, checkedItems
            ) { _, which, isChecked ->
                checkedItems[which] = isChecked
            }.setPositiveButton(
                "OK"
            ) { _, _ ->
                binding.include.tvIntervieweeFilter.text = ""

                selectedInterviewees = mutableListOf()

                for (i in checkedItems.indices) {
                    if (checkedItems[i]) {

                        binding.include.tvIntervieweeFilter.text =
                            "${binding.include.tvIntervieweeFilter.text} ${selectedItems[i]}, "

                        intervieweeList.find {
                            "${it.name} ${it.lastName}" == selectedItems[i]
                        }?.let { interviewee ->
                            selectedInterviewees.add(interviewee)
                        }
                    }
                }
            }
            .setNegativeButton(getString(R.string.DIALOG_NEGATIVE_BTN)) { dialog, _ ->
                dialog.dismiss()
            }
            .setNeutralButton(getString(R.string.DIALOG_NEUTRAL_BTN_CLEAR)) { _, _ ->
                for (i in checkedItems.indices) {
                    checkedItems[i] = false
                }
                binding.include.tvIntervieweeFilter.text =
                    getString(R.string.interviewees)
                selectedInterviewees.clear()
            }.create()
            .show()
    }

    private fun configFilterBottomSheet() {

        setSpinnerGenre()

        val behavior = BottomSheetBehavior.from(binding.include.filterBottomSheet)

        behavior.isHideable = false

        with(binding.include) {

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


                statsViewModel.getStats(
                    "Bearer $token",
                    idSelectedEmoticon,
                    genreLetter,
                    selectedInterviewees
                )

                Toast.makeText(requireContext(), "Filter btn clicked", Toast.LENGTH_SHORT).show()
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
            getString(R.string.SEXO_ALL),
            getString(R.string.SEXO_MASCULINO),
            getString(R.string.SEXO_FEMENINO),
            getString(R.string.SEXO_OTRO)
        )
        val adapterSexo =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, genreArray)

        binding.include.etIntervieweeGenre.apply {
            setAdapter<ArrayAdapter<String>>(adapterSexo)
            setText(genreArray[0], false)
        }

    }

    private fun setEmoticonEventsChart(eventsByEmotions: EventsByEmotionsDTO) {

        with(binding) {
            when {
                isRefresh -> {
                    emoticonEventsPieChart.aa_onlyRefreshTheChartDataWithChartOptionsSeriesArray(
                        arrayOf(
                            AASeriesElement()
                                .name("Valor")
                                .data(
                                    arrayOf(
                                        arrayOf(
                                            getString(R.string.happiness),
                                            eventsByEmotions.happy
                                        ),
                                        arrayOf(
                                            getString(R.string.sadness),
                                            eventsByEmotions.sad
                                        ),
                                        arrayOf(
                                            getString(R.string.fear),
                                            eventsByEmotions.fear
                                        ),
                                        arrayOf(
                                            getString(R.string.anger),
                                            eventsByEmotions.angry
                                        )
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
                        .colorsTheme(
                            arrayOf(
                                "#0c9674",
                                "#7dffc0",
                                "#d11b5f",
                                "#facd32",
                                "#ffffa0"
                            )
                        )
                        .dataLabelsEnabled(true)
                        .tooltipEnabled(true)
                        .series(
                            arrayOf(
                                AASeriesElement()
                                    .name("Valor")
                                    .data(
                                        arrayOf(
                                            arrayOf(
                                                getString(R.string.happiness),
                                                eventsByEmotions.happy
                                            ),
                                            arrayOf(
                                                getString(R.string.sadness),
                                                eventsByEmotions.sad
                                            ),
                                            arrayOf(
                                                getString(R.string.fear),
                                                eventsByEmotions.fear
                                            ),
                                            arrayOf(
                                                getString(R.string.anger),
                                                eventsByEmotions.angry
                                            )
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
                                        arrayOf(
                                            getString(R.string.SEXO_MASCULINO),
                                            intervieweeByGenre.totalMen
                                        ),
                                        arrayOf(
                                            getString(R.string.SEXO_FEMENINO),
                                            intervieweeByGenre.totalWomen
                                        ),
                                        arrayOf(
                                            getString(R.string.SEXO_OTRO),
                                            intervieweeByGenre.totalOther
                                        )
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
                        .colorsTheme(
                            arrayOf(
                                "#0c9674",
                                "#7dffc0",
                                "#d11b5f",
                                "#facd32",
                                "#ffffa0"
                            )
                        )
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
                                            arrayOf(
                                                getString(R.string.SEXO_MASCULINO),
                                                intervieweeByGenre.totalMen
                                            ),
                                            arrayOf(
                                                getString(R.string.SEXO_FEMENINO),
                                                intervieweeByGenre.totalWomen
                                            ),
                                            arrayOf(
                                                getString(R.string.SEXO_OTRO),
                                                intervieweeByGenre.totalOther
                                            )
                                        )
                                    )
                            )
                        )
                    genderPieChart.aa_drawChartWithChartModel(aaChartModelGender)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = StatsFragment()
    }
}