package com.keepcoding.blehealth.data.mock

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class DemoScenario(val label: String, val description: String) {
    NORMAL("正常成功", "扫描、连接、同步和保存全部成功"),
    NO_DEVICES("扫描无设备", "扫描结束后返回空列表"),
    CONNECTION_FAILURE("连接失败", "设备可见，但连接返回失败"),
    SYNC_FAILURE("同步失败", "连接成功，但健康数据同步失败"),
    PARTIAL_DATA("部分数据", "缺少睡眠数据，其余指标保存"),
    STORAGE_FAILURE("存储失败", "同步成功，但模拟数据库写入失败"),
    DUPLICATE_DATA("重复数据", "连续同步复用同一记录 ID，验证 Room 去重"),
}

class ScenarioController {
    private val _scenario = MutableStateFlow(DemoScenario.NORMAL)
    val scenario: StateFlow<DemoScenario> = _scenario.asStateFlow()

    fun select(value: DemoScenario) {
        _scenario.value = value
    }
}
