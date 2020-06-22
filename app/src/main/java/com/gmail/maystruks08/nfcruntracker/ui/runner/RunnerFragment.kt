package com.gmail.maystruks08.nfcruntracker.ui.runner

import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.gmail.maystruks08.nfcruntracker.App
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.base.BaseFragment
import com.gmail.maystruks08.nfcruntracker.core.base.FragmentToolbar
import com.gmail.maystruks08.nfcruntracker.core.ext.argument
import com.gmail.maystruks08.nfcruntracker.core.ext.injectViewModel
import kotlinx.android.synthetic.main.fragment_runner.*

class RunnerFragment : BaseFragment(R.layout.fragment_runner) {

    lateinit var viewModel: RunnerViewModel

    private var alertDialog: AlertDialog? = null

    private var runnerId: String by argument()

    private var runnerType: Int by argument()

    private var checkpointsAdapter: CheckpointsAdapter? = null

    override fun injectDependencies() {
        App.runnerComponent?.inject(this)
        viewModel = injectViewModel(viewModeFactory)
    }

    override fun initToolbar() = FragmentToolbar.Builder()
        .withId(R.id.toolbar)
        .withNavigationIcon(R.drawable.ic_arrow_back) { viewModel.onBackClicked() }
        .withTitle(R.string.screen_runner)
        .build()

    override fun bindViewModel() {
        viewModel.onShowRunnerClicked(runnerId)

        viewModel.runner.observe(viewLifecycleOwner, Observer { runner ->
            val numberStr = "#" + runner.number
            tvRunnerNumber.text = numberStr
            tvRunnerFullName.text = runner.fullName
            tvDateOfBirthday.text = runner.dateOfBirthday
            tvRunnerCity.text = runner.city
            if (runner.result != null) {
                val totalResultStr = getString(R.string.total_time, runner.result)
                btnMarkCheckpointAsPassedInManual.text = totalResultStr
                btnMarkCheckpointAsPassedInManual.isEnabled = false
            } else {
                btnMarkCheckpointAsPassedInManual.text = getString(R.string.mark_at_current_checkpoint)
                btnMarkCheckpointAsPassedInManual.isEnabled = true
            }
            checkpointsAdapter?.checkpoints = runner.progress.toMutableList()
        })
    }

    override fun initViews() {
        btnMarkCheckpointAsPassedInManual.setOnClickListener {
            val builder = AlertDialog.Builder(it.context)
                .setTitle(getString(R.string.attention))
                .setMessage(getString(R.string.mark_runner_without_card))
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    viewModel.markCheckpointAsPassed(runnerId)
                    alertDialog?.dismiss()
                }
                .setNegativeButton(android.R.string.no) { _, _ ->
                    alertDialog?.dismiss()
                }
            alertDialog = builder.show()
        }

        checkpointsAdapter = CheckpointsAdapter(::onCheckpointDateLongClicked)
        runnerCheckpointsRecyclerView.apply {
            layoutManager = LinearLayoutManager(runnerCheckpointsRecyclerView.context)
            adapter = checkpointsAdapter
        }
    }

    private fun onCheckpointDateLongClicked(checkpointId: Int) {
        val builder = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.attention))
            .setMessage(getString(R.string.remove_checkpoint_for_runner))
            .setPositiveButton(android.R.string.yes) { _, _ ->
                viewModel.deleteCheckpointFromRunner(runnerId, checkpointId)
                alertDialog?.dismiss()
            }
            .setNegativeButton(android.R.string.no) { _, _ ->
                alertDialog?.dismiss()
            }
        alertDialog = builder.show()
    }

    override fun onDestroyView() {
        App.clearRunnerComponent()
        super.onDestroyView()
    }

    companion object {

        fun getInstance(runnerId: String, runnerType: Int) = RunnerFragment().apply {
            this.runnerId = runnerId
            this.runnerType = runnerType
        }
    }
}
