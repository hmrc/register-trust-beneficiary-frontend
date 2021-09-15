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
import controllers.register.beneficiaries.companyoremploymentrelated.employmentRelated.routes._
import models.core.pages.{Description, InternationalAddress, UKAddress}
import models.registration.pages.HowManyBeneficiaries
import pages.register.beneficiaries.companyoremploymentrelated.employmentRelated._
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

class EmploymentRelatedBeneficiaryAnswersHelperSpec extends SpecBase {

  private val index: Int = 0
  private val name: String = "Name"
  private val ukAddress: UKAddress = UKAddress("Line 1", "Line 2", Some("Line 3"), Some("Line 4"), "AB11AB")
  private val country: String = "FR"
  private val nonUkAddress: InternationalAddress = InternationalAddress("Line 1", "Line 2", Some("Line 3"), country)
  private val description: Description = Description("Description", None, None, None, None)
  private val canEdit: Boolean = true

  private val helper: EmploymentRelatedBeneficiaryAnswersHelper = injector.instanceOf[EmploymentRelatedBeneficiaryAnswersHelper]

  "Employment-related Beneficiary answers helper" must {

    "return None for empty user answers" in {

      val result = helper.beneficiaries(emptyUserAnswers)

      result mustBe None
    }

    "return an employment-related beneficiary answer section" in {

      val userAnswers = emptyUserAnswers
        .set(LargeBeneficiaryNamePage(index), name).success.value
        .set(CountryOfResidenceYesNoPage(index), true).success.value
        .set(CountryOfResidenceInTheUkYesNoPage(index), false).success.value
        .set(CountryOfResidencePage(index), country).success.value
        .set(LargeBeneficiaryAddressYesNoPage(index), true).success.value
        .set(LargeBeneficiaryAddressUKYesNoPage(index), true).success.value
        .set(LargeBeneficiaryAddressPage(index), ukAddress).success.value
        .set(LargeBeneficiaryAddressInternationalPage(index), nonUkAddress).success.value
        .set(LargeBeneficiaryDescriptionPage(index), description).success.value
        .set(LargeBeneficiaryNumberOfBeneficiariesPage(index), HowManyBeneficiaries.Over201).success.value

      val result = helper.beneficiaries(userAnswers).get

      result mustBe
        Seq(
          AnswerSection(
            headingKey = Some("answerPage.section.largeBeneficiary.subheading"),
            rows = Seq(
              AnswerRow("employmentRelatedBeneficiary.name.checkYourAnswersLabel", Html(name), Some(NameController.onPageLoad(index, fakeDraftId).url), name, canEdit),
              AnswerRow("employmentRelatedBeneficiary.countryOfResidenceYesNo.checkYourAnswersLabel", Html("Yes"), Some(CountryOfResidenceYesNoController.onPageLoad(index, fakeDraftId).url), name, canEdit),
              AnswerRow("employmentRelatedBeneficiary.countryOfResidenceInTheUkYesNo.checkYourAnswersLabel", Html("No"), Some(CountryOfResidenceInTheUkYesNoController.onPageLoad(index, fakeDraftId).url), name, canEdit),
              AnswerRow("employmentRelatedBeneficiary.countryOfResidence.checkYourAnswersLabel", Html("France"), Some(CountryOfResidenceController.onPageLoad(index, fakeDraftId).url), name, canEdit),
              AnswerRow("employmentRelatedBeneficiary.addressYesNo.checkYourAnswersLabel", Html("Yes"), Some(AddressYesNoController.onPageLoad(index, fakeDraftId).url), name, canEdit),
              AnswerRow("employmentRelatedBeneficiary.addressUkYesNo.checkYourAnswersLabel", Html("Yes"), Some(AddressUkYesNoController.onPageLoad(index, fakeDraftId).url), name, canEdit),
              AnswerRow("employmentRelatedBeneficiary.ukAddress.checkYourAnswersLabel", Html("Line 1<br />Line 2<br />Line 3<br />Line 4<br />AB11AB"), Some(UkAddressController.onPageLoad(index, fakeDraftId).url), name, canEdit),
              AnswerRow("employmentRelatedBeneficiary.nonUkAddress.checkYourAnswersLabel", Html("Line 1<br />Line 2<br />Line 3<br />France"), Some(NonUkAddressController.onPageLoad(index, fakeDraftId).url), name, canEdit),
              AnswerRow("employmentRelatedBeneficiary.description.checkYourAnswersLabel", Html("Description"), Some(DescriptionController.onPageLoad(index, fakeDraftId).url), "", canEdit),
              AnswerRow("employmentRelatedBeneficiary.numberOfBeneficiaries.checkYourAnswersLabel", Html("201 to 500"), Some(NumberOfBeneficiariesController.onPageLoad(index, fakeDraftId).url), "", canEdit)
            ),
            headingArgs = Seq(index + 1)
          )
        )
    }
  }
}
