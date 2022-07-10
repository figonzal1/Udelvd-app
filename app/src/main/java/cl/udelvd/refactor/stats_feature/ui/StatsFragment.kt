package cl.udelvd.refactor.stats_feature.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import cl.udelvd.R
import cl.udelvd.databinding.FragmentNewStatsBinding
import cl.udelvd.refactor.emoticons_feature.domain.model.Emoticon
import cl.udelvd.refactor.interviewee_feature.domain.model.Interviewee
import cl.udelvd.refactor.project_feature.domain.model.Project
import cl.udelvd.refactor.stats_feature.data.remote.dto.EventsByEmotionsDTO
import cl.udelvd.refactor.stats_feature.data.remote.dto.IntervieweeGenreDTO
import cl.udelvd.utils.Utils
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

    private var _binding: FragmentNewStatsBinding? = null
    private val binding get() = _binding!!

    private var token: String? = null

    //EMOTICONES
    private var idSelectedEmoticon: Int = -1
    private var emoticonList: List<Emoticon> = arrayListOf()

    //Entrevistados
    private var selectedInterviewees: MutableList<Interviewee> = mutableListOf()
    private var listIntervieweeItems: Array<String> = arrayOf()
    private lateinit var checkedIntervieweesItems: BooleanArray

    //Proyectos
    private var selectedProjects: MutableList<Project> = mutableListOf()
    private var listProjectItems: Array<String> = arrayOf()
    private lateinit var checkedProjectItems: BooleanArray


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewStatsBinding.inflate(inflater, container, false)

        statsViewModel = ViewModelProvider(
            requireActivity(),
            ViewModelFactory(requireActivity().application)
        )[StatsViewModel::class.java]

        val sharedPreferences = requireContext().getSharedPreferences(
            getString(R.string.SHARED_PREF_MASTER_KEY),
            Context.MODE_PRIVATE
        )
        token = sharedPreferences.getString(
            getString(R.string.SHARED_PREF_TOKEN_LOGIN),
            ""
        )

        processErrorStates()

        processIntervieweeList()

        processEmoticonsList()

        processProjects()

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

    private fun processEmoticonsList() {
        viewLifecycleOwner.lifecycleScope.launch {

            statsViewModel.emoticonState
                .flowWithLifecycle(lifecycle, Lifecycle.State.CREATED)
                .collect {

                    when {
                        it.isLoading -> {

                            with(binding.include) {

                                //Show progress bar
                                pbEmoticons.visibility = View.VISIBLE

                                //HIDE EMOJIS
                                View.GONE.apply {
                                    ivAfraid.visibility = this
                                    ivHappy.visibility = this
                                    ivAngry.visibility = this
                                    ivSad.visibility = this

                                    emoticonRadioGroup.visibility = this
                                }
                            }
                        }
                        !it.isLoading && it.emoticonList.isNotEmpty() -> {

                            with(binding.include) {

                                //Show progress bar
                                pbEmoticons.visibility = View.GONE

                                //HIDE EMOJIS
                                View.VISIBLE.apply {
                                    ivAfraid.visibility = this
                                    ivHappy.visibility = this
                                    ivAngry.visibility = this
                                    ivSad.visibility = this

                                    emoticonRadioGroup.visibility = this
                                }

                                emoticonList = it.emoticonList.toMutableList()
                            }
                        }
                    }
                }
        }

        statsViewModel.getEmoticons("Bearer $token", Utils.getLanguage(requireContext()))
    }

    private fun processErrorStates() {
        viewLifecycleOwner.lifecycleScope.launch {

            statsViewModel.errorState
                .flowWithLifecycle(lifecycle, Lifecycle.State.CREATED)
                .collect {
                    Timber.d("Show error: $it")
                }
        }
    }

    private fun processIntervieweeList() {
        viewLifecycleOwner.lifecycleScope.launch {

            statsViewModel.intervieweeState
                .flowWithLifecycle(lifecycle, Lifecycle.State.CREATED)
                .collect { state ->

                    when {
                        state.isLoading -> {
                            with(binding.include) {
                                tvIntervieweeFilter.isEnabled = false
                                ivIntervieweeFilter.isEnabled = false

                                pbInterviewees.visibility = View.VISIBLE
                                ivIntervieweeFilter.visibility = View.GONE
                            }
                        }
                        !state.isLoading && state.interviewee.isNotEmpty() -> {

                            listIntervieweeItems = state.interviewee.map {
                                "${it.name} ${it.lastName}"
                            }.toTypedArray()
                            checkedIntervieweesItems = BooleanArray(state.interviewee.size)

                            with(binding.include) {
                                tvIntervieweeFilter.isEnabled = true
                                ivIntervieweeFilter.isEnabled = true

                                ivIntervieweeFilter.visibility = View.VISIBLE
                                pbInterviewees.visibility = View.GONE

                                ivIntervieweeFilter.setOnClickListener {
                                    configIntervieweeFilterDialog(
                                        state.interviewee,
                                        listIntervieweeItems.toList()
                                    )
                                }
                                tvIntervieweeFilter.setOnClickListener {
                                    configIntervieweeFilterDialog(
                                        state.interviewee,
                                        listIntervieweeItems.toList()
                                    )
                                }
                            }
                        }
                    }
                }
        }
        statsViewModel.getIntervieweeWithEvents("Bearer $token", selectedProjects)
    }

    private fun processProjects() {
        viewLifecycleOwner.lifecycleScope.launch {

            statsViewModel.projectState
                .flowWithLifecycle(lifecycle, Lifecycle.State.CREATED)
                .collect { state ->

                    when {
                        state.isLoading -> {
                            with(binding.include) {
                                tvProjectFilter.isEnabled = false
                                ivProjectFilter.isEnabled = false

                                ivProjectFilter.visibility = View.GONE
                                pbProjects.visibility = View.VISIBLE
                            }
                        }
                        !state.isLoading && state.projectList.isNotEmpty() -> {
                            listProjectItems = state.projectList.map {
                                it.name
                            }.toTypedArray()
                            checkedProjectItems = BooleanArray(state.projectList.size)

                            with(binding.include) {
                                tvProjectFilter.isEnabled = true
                                ivProjectFilter.isEnabled = true

                                ivProjectFilter.visibility = View.VISIBLE
                                pbProjects.visibility = View.GONE

                                ivProjectFilter.setOnClickListener {
                                    configProjectFilterDialog(
                                        state.projectList,
                                        listProjectItems.toList()
                                    )
                                }
                                tvProjectFilter.setOnClickListener {
                                    configProjectFilterDialog(
                                        state.projectList,
                                        listProjectItems.toList()
                                    )
                                }
                            }
                        }
                    }
                }
        }
        statsViewModel.getProjects("Bearer $token")
    }

    private fun processStatsData() {

        viewLifecycleOwner.lifecycleScope.launch {

            statsViewModel.statsState
                .flowWithLifecycle(lifecycle, Lifecycle.State.CREATED)
                .collect { statsState ->

                    when {
                        statsState.isLoading -> {
                            binding.progressBarStats.visibility = View.VISIBLE
                            binding.tvStatsMessage.visibility = View.GONE
                            binding.ivBottomMenu.visibility = View.GONE

                        }
                        !statsState.isLoading && statsState.stats != null -> {

                            with(statsState.stats) {

                                binding.generalStats.visibility = View.VISIBLE
                                binding.progressBarStats.visibility = View.GONE

                                basicInformation.apply {

                                    binding.nInterviewee.text = String.format(
                                        getString(R.string.n_entrevistados),
                                        nInterviewees
                                    )
                                    binding.nEvents.text =
                                        String.format(getString(R.string.n_eventos), nEvents)
                                }

                                setGenderChart(intervieweeByGenre)

                                setEmoticonEventsChart(eventsByEmotions)

                                isRefresh = true
                            }
                        }
                    }
                }
        }
    }

    /**
     * Main config for bottom sheet
     */
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
                    selectedProjects,
                    selectedInterviewees
                )
            }

            btnClear.setOnClickListener {

                //INTERVIEWEES
                tvIntervieweeFilter.text = getString(R.string.interviewees)
                for (i in checkedIntervieweesItems.indices) {
                    checkedIntervieweesItems[i] = false
                }
                selectedInterviewees.clear()

                //EMOTICONS
                emoticonRadioGroup.clearCheck()

                //GENDER
                etIntervieweeGenre.setText(
                    etIntervieweeGenre.adapter.getItem(0).toString(),
                    false
                )

                //CLEAR Projects
                for (i in checkedProjectItems.indices) {
                    checkedProjectItems[i] = false
                }
                binding.include.tvProjectFilter.text =
                    getString(R.string.proyectos)
                selectedProjects.clear()

            }
        }
    }

    /**
     * Configuration for emoticon radioGroup
     */
    private fun findEmoticonId(checkedId: Int): Int {
        return when (checkedId) {
            R.id.radio_happy -> emoticonList.find { it.description.contains(getString(R.string.happiness)) }!!.id
            R.id.radio_angry -> emoticonList.find { it.description.contains(getString(R.string.anger)) }!!.id
            R.id.radio_fear -> emoticonList.find { it.description.contains(getString(R.string.fear)) }!!.id
            R.id.radio_sad -> emoticonList.find { it.description.contains(getString(R.string.sadness)) }!!.id
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

    private fun configProjectFilterDialog(
        projectList: List<Project>,
        selectedItems: List<String>
    ) {
        AlertDialog.Builder(requireActivity())
            .setTitle(getString(R.string.select_project))
            .setCancelable(false)
            .setMultiChoiceItems(listProjectItems, checkedProjectItems) { _, which, isChecked ->
                checkedProjectItems[which] = isChecked
            }.setPositiveButton(
                getString(R.string.ok)
            ) { _, _ ->
                binding.include.tvProjectFilter.text = ""

                selectedProjects = mutableListOf()

                for (i in checkedProjectItems.indices) {
                    if (checkedProjectItems[i]) {

                        binding.include.tvProjectFilter.text =
                            "${binding.include.tvProjectFilter.text} ${selectedItems[i]}, "

                        projectList.find {
                            it.name == selectedItems[i]
                        }?.let { project ->
                            selectedProjects.add(project)
                        }
                    }
                }

                //REMOVE last comma
                binding.include.tvProjectFilter.apply {
                    text = text.dropLast(2)
                }

                //CLEAN INTERVIEWEES SELECTION
                for (i in checkedIntervieweesItems.indices) {
                    checkedIntervieweesItems[i] = false
                }
                binding.include.tvIntervieweeFilter.text =
                    getString(R.string.interviewees)
                selectedInterviewees.clear()

                statsViewModel.getIntervieweeWithEvents("Bearer $token", selectedProjects)
            }
            .setNegativeButton(getString(R.string.DIALOG_NEGATIVE_BTN)) { dialog, _ ->
                dialog.dismiss()
            }
            .setNeutralButton(getString(R.string.DIALOG_NEUTRAL_BTN_CLEAR)) { _, _ ->
                for (i in checkedProjectItems.indices) {
                    checkedProjectItems[i] = false
                }
                binding.include.tvProjectFilter.text =
                    getString(R.string.proyectos)
                selectedProjects.clear()

                statsViewModel.getIntervieweeWithEvents("Bearer $token", selectedProjects)
            }.create()
            .show()
    }

    /**
     * function tha build AlertDialog for interviewee
     */
    private fun configIntervieweeFilterDialog(
        intervieweeList: List<Interviewee>,
        selectedItems: List<String>
    ) {

        AlertDialog.Builder(requireActivity())
            .setTitle(getString(R.string.title_dialog_selected_interviewee))
            .setCancelable(false)
            .setMultiChoiceItems(
                listIntervieweeItems, checkedIntervieweesItems
            ) { _, which, isChecked ->
                checkedIntervieweesItems[which] = isChecked
            }.setPositiveButton(
                getString(R.string.ok)
            ) { _, _ ->
                binding.include.tvIntervieweeFilter.text = ""

                selectedInterviewees = mutableListOf()

                for (i in checkedIntervieweesItems.indices) {
                    if (checkedIntervieweesItems[i]) {

                        binding.include.tvIntervieweeFilter.text =
                            "${binding.include.tvIntervieweeFilter.text} ${selectedItems[i]}, "

                        intervieweeList.find {
                            "${it.name} ${it.lastName}" == selectedItems[i]
                        }?.let { interviewee ->
                            selectedInterviewees.add(interviewee)
                        }
                    }
                }
                binding.include.tvIntervieweeFilter.apply {
                    text = text.dropLast(2)
                }
            }
            .setNegativeButton(getString(R.string.DIALOG_NEGATIVE_BTN)) { dialog, _ ->
                dialog.dismiss()
            }
            .setNeutralButton(getString(R.string.DIALOG_NEUTRAL_BTN_CLEAR)) { _, _ ->
                for (i in checkedIntervieweesItems.indices) {
                    checkedIntervieweesItems[i] = false
                }
                binding.include.tvIntervieweeFilter.text =
                    getString(R.string.interviewees)
                selectedInterviewees.clear()
            }.create()
            .show()
    }

    private fun setEmoticonEventsChart(eventsByEmotions: EventsByEmotionsDTO) {

        with(binding) {

            emoticonEventsPieChart.visibility = View.VISIBLE

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

            genderPieChart.visibility = View.VISIBLE

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