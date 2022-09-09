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

package utils.answers

import base.SpecBase
import controllers.register.beneficiaries.companyoremploymentrelated.employmentRelated.mld5.routes._
import controllers.register.beneficiaries.companyoremploymentrelated.employmentRelated.routes._
import models.core.pages.{Description, InternationalAddress, UKAddress}
import models.registration.pages.HowManyBeneficiaries
import pages.register.beneficiaries.companyoremploymentrelated.employmentRelated._
import pages.register.beneficiaries.companyoremploymentrelated.employmentRelated.mld5._
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
        .set(LargeBeneficiaryNamePage(index), name).right.get
        .set(CountryOfResidenceYesNoPage(index), true).right.get
        .set(CountryOfResidenceInTheUkYesNoPage(index), false).right.get
        .set(CountryOfResidencePage(index), country).right.get
        .set(LargeBeneficiaryAddressYesNoPage(index), true).right.get
        .set(LargeBeneficiaryAddressUKYesNoPage(index), true).right.get
        .set(LargeBeneficiaryAddressPage(index), ukAddress).right.get
        .set(LargeBeneficiaryAddressInternationalPage(index), nonUkAddress).right.get
        .set(LargeBeneficiaryDescriptionPage(index), description).right.get
        .set(LargeBeneficiaryNumberOfBeneficiariesPage(index), HowManyBeneficiaries.Over201).right.get

      val result = helper.beneficiaries(userAnswers).get

      result mustBe
        Seq(
          AnswerSection(
            headingKey = Some("answerPage.section.largeBeneficiary.subheading"),
            rows = Seq(
              AnswerRow("employmentRelatedBeneficiary.name.checkYourAnswersLabel", Html(name), Some(NameController.onPageLoad(index, fakeDraftId).url), name, canEdit),
              AnswerRow("employmentRelatedBeneficiary.5mld.countryOfResidenceYesNo.checkYourAnswersLabel", Html("Yes"), Some(CountryOfResidenceYesNoController.onPageLoad(index, fakeDraftId).url), name, canEdit),
              AnswerRow("employmentRelatedBeneficiary.5mld.countryOfResidenceInTheUkYesNo.checkYourAnswersLabel", Html("No"), Some(CountryOfResidenceInTheUkYesNoController.onPageLoad(index, fakeDraftId).url), name, canEdit),
              AnswerRow("employmentRelatedBeneficiary.5mld.countryOfResidence.checkYourAnswersLabel", Html("France"), Some(CountryOfResidenceController.onPageLoad(index, fakeDraftId).url), name, canEdit),
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
