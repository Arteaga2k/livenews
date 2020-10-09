package dev.arteaga.breakingnews.util

import dev.arteaga.breakingnews.domain.scheduler.SchedulerProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class AndroidSchedulerProvider :
    SchedulerProvider.Factory(Schedulers.io(), AndroidSchedulers.mainThread())