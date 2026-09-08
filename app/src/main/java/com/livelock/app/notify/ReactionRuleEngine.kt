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

    fun evaluate(packageName: String, title: String, text: String) {
        for (rule in rules) {
            if (!rule.enabled) continue
            val matchesApp = rule.appName.isNotEmpty() && packageName.contains(rule.appName)
            val matchesKeyword = rule.keyword.isNotEmpty() && (title.contains(rule.keyword) || text.contains(rule.keyword))
            if (matchesApp || matchesKeyword) {
                triggerReaction(rule.animationIndex, rule.showBubble, title)
                return
            }
        }
        triggerReaction(1, false, null) // default reaction
    }

    private fun triggerReaction(animationIndex: Int, showBubble: Boolean, text: String?) {
        // TODO: Send event to wallpaper service
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
