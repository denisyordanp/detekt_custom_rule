package org.example.detekt

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import org.jetbrains.kotlin.asJava.namedUnwrappedElement
import org.jetbrains.kotlin.psi.KtEnumEntry
import java.io.File

class EnumValueFormat(config: Config) : Rule(config) {

    companion object {
        private const val ENUM_VALUE_NAME_REGEX = "\"([^\"]+)\""
        private const val SNAKE_CASE_REGEX = "[a-z]+(_[a-z]+)*"
        private const val DEFAULT_MAX_CHARACTER_LENGTH = 40
    }

    @Configuration("fileNames")
    private val fileNames by config(emptyList<String>())

    @Configuration("maxValueLength")
    private val snakeCaseActive by config(true)

    @Configuration("maxValueLength")
    private val maxValueLength by config(DEFAULT_MAX_CHARACTER_LENGTH)

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Style,
        "Enum value doesn't comply with maverick formatting.",
        Debt.FIVE_MINS
    )

    override fun visitEnumEntry(enumEntry: KtEnumEntry) {
        super.visitEnumEntry(enumEntry)
        if (enumEntry.className() in fileNames) {
            if (snakeCaseActive && !enumEntry.getFirstEnumValue().isSnakeCase()) {
                report(CodeSmell(issue, Entity.from(element = enumEntry), "This Enum value should use snake_case format."))
            } else if (enumEntry.getFirstEnumValue().length > maxValueLength) {
                report(CodeSmell(issue, Entity.from(element = enumEntry), "This Enum value should not more than $maxValueLength character."))
            } else Unit
        }
    }

    private fun KtEnumEntry.getFirstEnumValue() = this.children.firstOrNull()?.text?.let {
        ENUM_VALUE_NAME_REGEX.toRegex().find(it)?.groupValues?.getOrNull(1)
    } ?: ""

    private fun KtEnumEntry.className(): String =
        this.parent.namedUnwrappedElement?.name?.let {
            if (it.contains(File.separatorChar)) {
                it.substringAfterLast(File.separatorChar)
            } else {
                it
            }
        } ?: ""

    private fun String.isSnakeCase() =
        if (this.isEmpty()) true else this.matches(Regex(SNAKE_CASE_REGEX))
}
