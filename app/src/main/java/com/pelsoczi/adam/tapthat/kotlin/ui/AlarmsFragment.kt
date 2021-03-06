package com.pelsoczi.adam.tapthat.kotlin.ui

import android.arch.lifecycle.Observer
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearLayoutManager.VERTICAL
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.ViewSwitcher
import com.pelsoczi.adam.tapthat.R
import com.pelsoczi.adam.tapthat.ui.Adapter
import com.pelsoczi.adam.tapthat.util.Format
import com.pelsoczi.data.Alarm
import kotlinx.android.synthetic.main.activity_application.*
import kotlinx.android.synthetic.main.alarms_fragment.*

class AlarmsFragment : Fragment() {

    companion object {
        val NAME = AlarmsFragment::class.java.simpleName
    }
    init {
        Log.wtf(NAME, "init{$NAME}")
    }

    private val factory = ViewSwitcher.ViewFactory {
        val textView = TextView(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            textView.setTextAppearance(R.style.TextAppearance_AppCompat_Medium)
        textView
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return LayoutInflater.from(container?.context).inflate(R.layout.alarms_fragment,
                container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        with(ac_next_date) {
            setFactory(factory)
            setInAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in))
            setOutAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_out))
        }
        with(ac_next_time) {
            setFactory(factory)
            setInAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in))
            setOutAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_out))
        }

        with(alarm_container_recycler) {
            setHasFixedSize(true)
            adapter = Adapter(context, listOf(Alarm.EMPTY))
            layoutManager = LinearLayoutManager(activity, VERTICAL, false)
            itemAnimator = DefaultItemAnimator()
        }

        activity.floating_action_btn.setOnClickListener {
            view ->
            println("$NAME: floating_action_btn onClick")
            (activity as AlarmActivity).onAlarmClick(Alarm.EMPTY)
        }

        val alarmActivity = activity as AlarmActivity
        alarmActivity.viewModel.getAlarms().observe(alarmActivity, Observer<MutableList<Alarm>> {
            alarms ->
            if (alarms != null) {
                with(alarm_container_recycler) {
                    swapAdapter(Adapter(context, alarms), false)
                }
            }
        })
        alarmActivity.viewModel.getScheduled().observe(alarmActivity, Observer<Alarm> {
            alarm ->
            if (alarm != null) {
                if (alarm == Alarm.EMPTY) {
                    ac_next_date.setText(getString(R.string.title_next_empty))
                    ac_next_time.setText("")
                } else {
                    val millis = (activity as AlarmActivity).viewModel.getScheduledMillis()
                    ac_next_date.setText(Format.formatDate(millis))
                    ac_next_time.setText(Format.formatTime(millis))
                }
            }
        })
    }
}