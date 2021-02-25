package com.zk.lemopoc.di

import com.zk.lemopoc.features.chat.viewModel.ChatViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


class ViewModelsModule {
	companion object{
		val modules = module {
			viewModel { ChatViewModel(get()) }
		}
	}
}
