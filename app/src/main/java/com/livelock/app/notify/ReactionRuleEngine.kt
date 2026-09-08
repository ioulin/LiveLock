package com.livelock.app.notify

import android.content.Context
import android.content.SharedPreferences

data class ReactionRule(
    val id: String,
    val appName: String,
    val keyword: String,
    val animationIndex: Int,
    val showBubble: Boolean,
    val enabled: Boolean
)

class ReactionRuleEngine(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("reaction_rules", Context.MODE_PRIVATE)
    private val rules = mutableListOf<ReactionRule>()

    init {
        loadDefaultRules()
        loadRules()
    }

    private fun loadDefaultRules() {
        if (!prefs.contains("initialized")) {
            val defaults = listOf(
                ReactionRule("kakao", "com.kakao.talk", "", 2, true, true),
                ReactionRule("sms", "com.android.mms", "", 1, true, true),
                ReactionRule("urgent", "", "긴급", 0, true, true)
            )
            saveRules(defaults)
            prefs.edit().putBoolean("initialized", true).apply()
        }
    }

    private fun loadRules() {
        rules.clear()
        val count = prefs.getInt("rule_count", 0)
        for (i in 0 until count) {
            val rule = ReactionRule(
                id = prefs.getString("rule_${i}_id", "") ?: "",
                appName = prefs.getString("rule_${i}_app", "") ?: "",
                keyword = prefs.getString("rule_${i}_keyword", "") ?: "",
                animationIndex = prefs.getInt("rule_${i}_anim", 0),
                showBubble = prefs.getBoolean("rule_${i}_bubble", false),
                enabled = prefs.getBoolean("rule_${i}_enabled", true)
            )
            rules.add(rule)
        }
    }

    /** 매칭되는 규칙의 애니메이션 인덱스를 반환한다.
     *  (알림 반응 서비스가 이를 저장하고 월페이퍼가 클립을 전환) */
    fun evaluate(packageName: String, title: String, text: String): Int {
        for (rule in rules) {
            if (!rule.enabled) continue
            val matchesApp = rule.appName.isNotEmpty() && packageName.contains(rule.appName, ignoreCase = true)
            val matchesKeyword = rule.keyword.isNotEmpty() && (title.contains(rule.keyword, ignoreCase = true) || text.contains(rule.keyword, ignoreCase = true))
            if (matchesApp || matchesKeyword) {
                return rule.animationIndex
            }
        }
        return 1 // default reaction
    }

    fun getRules(): List<ReactionRule> = rules.toList()

    fun addRule(rule: ReactionRule) {
        rules.add(rule)
        saveRules(rules)
    }

    fun removeRule(id: String) {
        rules.removeAll { it.id == id }
        saveRules(rules)
    }

    private fun saveRules(ruleList: List<ReactionRule>) {
        prefs.edit().apply {
            putInt("rule_count", ruleList.size)
            ruleList.forEachIndexed { i, rule ->
                putString("rule_${i}_id", rule.id)
                putString("rule_${i}_app", rule.appName)
                putString("rule_${i}_keyword", rule.keyword)
                putInt("rule_${i}_anim", rule.animationIndex)
                putBoolean("rule_${i}_bubble", rule.showBubble)
                putBoolean("rule_${i}_enabled", rule.enabled)
            }
            apply()
        }
    }
}
