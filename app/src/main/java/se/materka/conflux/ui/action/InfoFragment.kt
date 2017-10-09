package se.materka.conflux.ui.action

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.franmontiel.fullscreendialog.FullScreenDialogContent
import com.franmontiel.fullscreendialog.FullScreenDialogController
import kotlinx.android.synthetic.main.fragment_info.*
import se.materka.conflux.R
import se.materka.conflux.ui.list.ListViewModel

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

class InfoFragment : Fragment(), FullScreenDialogContent {
    private val listViewModel: ListViewModel by lazy {
        ViewModelProviders.of(activity).get(ListViewModel::class.java)
    }

    private val station by lazy {
        listViewModel.selected.value
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_info, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        text_name.text = station?.name
        text_url.text = station?.url
        text_format.text = station?.format
        text_bitrate.text = station?.bitrate.toString()
        text_genre.text = station?.genre
        text_channels.text = station?.channels.toString()

    }

    override fun onConfirmClick(dialogController: FullScreenDialogController?): Boolean {
        return false
    }

    override fun onDialogCreated(dialogController: FullScreenDialogController?) {}

    override fun onDiscardClick(dialogController: FullScreenDialogController?): Boolean {
        return false
    }
}