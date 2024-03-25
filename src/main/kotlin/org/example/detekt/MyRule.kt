package org.example.detekt

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtProperty

class EnumValueSnakeCaseNaming(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Style,
        "Enum value property should be named using snake_case format.",
        Debt.FIVE_MINS
    )

    override fun visitProperty(property: KtProperty) {
        super.visitProperty(property)
        if (property.isEnumValueProperty() && property.name?.isSnakeCase() == false) {
            report(CodeSmell(issue, Entity.from(element = property.parent), "Custom Message"))
        }
    }

    private fun KtProperty.isEnumValueProperty(): Boolean {
        val parentEnum = parent.children.find { it is KtClass } as? KtClass
        return parentEnum?.isEnum() == true && parentEnum.isValue() && hasInitializer()
    }

    private fun String.isSnakeCase(): Boolean {
        return this.matches(Regex("[a-z]+(_[a-z]+)*"))
    }
}
