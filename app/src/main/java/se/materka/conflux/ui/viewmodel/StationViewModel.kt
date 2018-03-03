package se.materka.conflux.ui.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import se.materka.conflux.db.entity.Station
import se.materka.conflux.db.repository.StationRepository


/**
 * Copyright 2017 Mattias Karlsson

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

class StationViewModel(application: Application, private val repository: StationRepository) : AndroidViewModel(application) {

    val selected: MutableLiveData<Station> = MutableLiveData()

    fun get(): LiveData<List<Station>>? {
        return repository.getStations()
    }

    fun save(station: Station): LiveData<Long> {
        return repository.create(station)
    }

    fun delete(station: Station): LiveData<Boolean> {
        return repository.delete(station)
    }

    fun update(station: Station) {
        repository.update(station)
    }

    fun select(station: Station) {
        selected.postValue(station)
    }
}