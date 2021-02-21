/*
 * Copyright 2021 HM Revenue & Customs
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

package utils.answers

import base.SpecBase
import controllers.register.beneficiaries.classofbeneficiaries.routes._
import pages.register.beneficiaries.classofbeneficiaries._
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

class ClassOfBeneficiaryAnswersHelperSpec extends SpecBase {

  private val index: Int = 0
  private val description: String = "Description"
  private val canEdit: Boolean = true

  private val helper: ClassOfBeneficiaryAnswersHelper = injector.instanceOf[ClassOfBeneficiaryAnswersHelper]

  "Class of Beneficiary answers helper" must {

    "return None for empty user answers" in {

      val result = helper.classOfBeneficiaries(emptyUserAnswers)

      result mustBe None
    }

    "return a class of beneficiary answer section" in {

      val userAnswers = emptyUserAnswers
        .set(ClassBeneficiaryDescriptionPage(index), description).success.value

      val result = helper.classOfBeneficiaries(userAnswers).get

      result mustBe
        Seq(
          AnswerSection(
            headingKey = Some(messages("answerPage.section.classOfBeneficiary.subheading", index + 1)),
            rows = Seq(
              AnswerRow("classBeneficiaryDescription.checkYourAnswersLabel", Html(description), Some(ClassBeneficiaryDescriptionController.onPageLoad(index, fakeDraftId).url), description, canEdit)
            )
          )
        )
    }
  }
}
