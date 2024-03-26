package org.example.detekt

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider

class MaverickDetektRuleSetProvider : RuleSetProvider {
    override val ruleSetId: String = "maverick"

    override fun instance(config: Config): RuleSet {
        return RuleSet(
            ruleSetId,
            listOf(
                EnumValueFormat(config),
            ),
        )
    }
}
