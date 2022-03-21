/*
 * Copyright 2022 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package utils.print

import com.google.inject.Inject
import models.UserAnswers
import models.core.pages.{Address, Description, FullName}
import models.registration.pages.{HowManyBeneficiaries, PassportOrIdCardDetails, RoleInCompany}
import play.api.i18n.Messages
import play.api.libs.json.Reads
import play.twirl.api.{Html, HtmlFormat}
import queries.Gettable
import utils.answers.CheckAnswersFormatters
import viewmodels.AnswerRow

import java.time.LocalDate

class AnswerRowConverter @Inject()(checkAnswersFormatters: CheckAnswersFormatters) {

  def bind(userAnswers: UserAnswers, name: String)
          (implicit messages: Messages): Bound = new Bound(userAnswers, name)

  class Bound(userAnswers: UserAnswers, name: String)(implicit messages: Messages) {

    def nameQuestion(query: Gettable[FullName],
                     labelKey: String,
                     changeUrl: String): Option[AnswerRow] = {
      val format = (x: FullName) => HtmlFormat.escape(x.displayFullName)
      question(query, labelKey, format, changeUrl)
    }

    def roleInCompanyQuestion(query: Gettable[RoleInCompany],
                              labelKey: String,
                              changeUrl: String): Option[AnswerRow] = {
      val format = (x: RoleInCompany) => checkAnswersFormatters.formatRoleInCompany(x)
      question(query, labelKey, format, changeUrl, name)
    }

    def stringQuestion(query: Gettable[String],
                       labelKey: String,
                       changeUrl: String): Option[AnswerRow] = {
      val format = (x: String) => HtmlFormat.escape(x)
      question(query, labelKey, format, changeUrl, name)
    }

    def countryQuestion(isUkQuery: Gettable[Boolean],
                        query: Gettable[String],
                        labelKey: String,
                        changeUrl: String): Option[AnswerRow] = {
      userAnswers.get(isUkQuery) flatMap {
        case false =>
          val format = (x: String) => HtmlFormat.escape(checkAnswersFormatters.country(x))
          question(query, labelKey, format, changeUrl, name)
        case _ =>
          None
      }
    }

    def percentageQuestion(query: Gettable[Int],
                           labelKey: String,
                           changeUrl: String): Option[AnswerRow] = {
      val format = (x: Int) => checkAnswersFormatters.percentage(x.toString)
      question(query, labelKey, format, changeUrl, name)
    }

    def intQuestion(query: Gettable[Int],
                    labelKey: String,
                    changeUrl: String): Option[AnswerRow] = {
      val format = (x: Int) => HtmlFormat.escape(x.toString)
      question(query, labelKey, format, changeUrl, name)
    }

    def yesNoQuestion(query: Gettable[Boolean],
                      labelKey: String,
                      changeUrl: String): Option[AnswerRow] = {
      val format = (x: Boolean) => checkAnswersFormatters.yesOrNo(x)
      question(query, labelKey, format, changeUrl, name)
    }

    def enumQuestion[T](query: Gettable[T],
                        labelKey: String,
                        changeUrl: String,
                        enumPrefix: String)
                       (implicit messages:Messages, rds: Reads[T]): Option[AnswerRow] = {
      val format = (x: T) => checkAnswersFormatters.formatEnum(enumPrefix, x)
      question(query, labelKey, format, changeUrl, name)
    }

    def dateQuestion(query: Gettable[LocalDate],
                     labelKey: String,
                     changeUrl: String): Option[AnswerRow] = {
      val format = (x: LocalDate) => checkAnswersFormatters.formatDate(x)
      question(query, labelKey, format, changeUrl, name)
    }

    def ninoQuestion(query: Gettable[String],
                     labelKey: String,
                     changeUrl: String): Option[AnswerRow] = {
      val format = (x: String) => checkAnswersFormatters.formatNino(x)
      question(query, labelKey, format, changeUrl, name)
    }

    def addressQuestion[T <: Address](query: Gettable[T],
                                      labelKey: String,
                                      changeUrl: String)
                                     (implicit reads: Reads[T]): Option[AnswerRow] = {
      val format = (x: T) => checkAnswersFormatters.addressFormatter(x)
      question(query, labelKey, format, changeUrl, name)
    }

    def passportDetailsQuestion(query: Gettable[PassportOrIdCardDetails],
                                labelKey: String,
                                changeUrl: String): Option[AnswerRow] = {
      val format = (x: PassportOrIdCardDetails) => checkAnswersFormatters.passportOrIDCard(x)
      question(query, labelKey, format, changeUrl, name)
    }

    def descriptionQuestion(query: Gettable[Description],
                            labelKey: String,
                            changeUrl: String): Option[AnswerRow] = {
      val format = (x: Description) => checkAnswersFormatters.formatDescription(x)
      question(query, labelKey, format, changeUrl)
    }

    def numberOfBeneficiariesQuestion(query: Gettable[HowManyBeneficiaries],
                                      labelKey: String,
                                      changeUrl: String): Option[AnswerRow] = {
      val format = (x: HowManyBeneficiaries) => checkAnswersFormatters.formatNumberOfBeneficiaries(x)
      question(query, labelKey, format, changeUrl)
    }

    private def question[T](query: Gettable[T],
                            labelKey: String,
                            format: T => Html,
                            changeUrl: String,
                            labelArg: String = "")
                           (implicit rds: Reads[T]): Option[AnswerRow] = {
      userAnswers.get(query) map { x =>
        AnswerRow(
          label = s"$labelKey.checkYourAnswersLabel",
          answer = format(x),
          changeUrl = Some(changeUrl),
          labelArg = labelArg
        )
      }
    }

  }
}
