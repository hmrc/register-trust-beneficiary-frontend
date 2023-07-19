/*
 * Copyright 2023 HM Revenue & Customs
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
import controllers.register.beneficiaries.companyoremploymentrelated.company.mld5.routes._
import controllers.register.beneficiaries.companyoremploymentrelated.company.routes._
import models.core.pages.{InternationalAddress, UKAddress}
import pages.register.beneficiaries.companyoremploymentrelated.company._
import pages.register.beneficiaries.companyoremploymentrelated.company.mld5._
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

class CompanyBeneficiaryAnswersHelperSpec extends SpecBase {

  private val index: Int = 0
  private val name: String = "Name"
  private val amount: Int = 50
  private val ukAddress: UKAddress = UKAddress("Line 1", "Line 2", Some("Line 3"), Some("Line 4"), "AB11AB")
  private val country: String = "FR"
  private val nonUkAddress: InternationalAddress = InternationalAddress("Line 1", "Line 2", Some("Line 3"), country)
  private val canEdit: Boolean = true

  private val helper: CompanyBeneficiaryAnswersHelper = injector.instanceOf[CompanyBeneficiaryAnswersHelper]

  "Company Beneficiary answers helper" must {

    "return None for empty user answers" in {

      val result = helper.beneficiaries(emptyUserAnswers)

      result mustBe None
    }

    "return a company beneficiary answer section" in {

      val userAnswers = emptyUserAnswers
        .set(NamePage(index), name).value
        .set(IncomeYesNoPage(index), false).value
        .set(IncomePage(index), amount).value
        .set(CountryOfResidenceYesNoPage(index), true).value
        .set(CountryOfResidenceInTheUkYesNoPage(index), false).value
        .set(CountryOfResidencePage(index), country).value
        .set(AddressYesNoPage(index), true).value
        .set(AddressUKYesNoPage(index), true).value
        .set(AddressUKPage(index), ukAddress).value
        .set(AddressInternationalPage(index), nonUkAddress).value

      val result = helper.beneficiaries(userAnswers).get

      result mustBe
        Seq(
          AnswerSection(
            headingKey = Some("answerPage.section.companyBeneficiary.subheading"),
            rows = Seq(
              AnswerRow("companyBeneficiary.name.checkYourAnswersLabel", Html(name), Some(NameController.onPageLoad(index, fakeDraftId).url), name, canEdit),
              AnswerRow("companyBeneficiary.discretionYesNo.checkYourAnswersLabel", Html("No"), Some(DiscretionYesNoController.onPageLoad(index, fakeDraftId).url), name, canEdit),
              AnswerRow("companyBeneficiary.shareOfIncome.checkYourAnswersLabel", Html("50%"), Some(ShareOfIncomeController.onPageLoad(index, fakeDraftId).url), name, canEdit),
              AnswerRow("companyBeneficiary.5mld.countryOfResidenceYesNo.checkYourAnswersLabel", Html("Yes"), Some(CountryOfResidenceYesNoController.onPageLoad(index, fakeDraftId).url), name, canEdit),
              AnswerRow("companyBeneficiary.5mld.countryOfResidenceInTheUkYesNo.checkYourAnswersLabel", Html("No"), Some(CountryOfResidenceInTheUkYesNoController.onPageLoad(index, fakeDraftId).url), name, canEdit),
              AnswerRow("companyBeneficiary.5mld.countryOfResidence.checkYourAnswersLabel", Html("France"), Some(CountryOfResidenceController.onPageLoad(index, fakeDraftId).url), name, canEdit),
              AnswerRow("companyBeneficiary.addressYesNo.checkYourAnswersLabel", Html("Yes"), Some(AddressYesNoController.onPageLoad(index, fakeDraftId).url), name, canEdit),
              AnswerRow("companyBeneficiary.addressUkYesNo.checkYourAnswersLabel", Html("Yes"), Some(AddressUkYesNoController.onPageLoad(index, fakeDraftId).url), name, canEdit),
              AnswerRow("companyBeneficiary.ukAddress.checkYourAnswersLabel", Html("Line 1<br />Line 2<br />Line 3<br />Line 4<br />AB11AB"), Some(UkAddressController.onPageLoad(index, fakeDraftId).url), name, canEdit),
              AnswerRow("companyBeneficiary.nonUkAddress.checkYourAnswersLabel", Html("Line 1<br />Line 2<br />Line 3<br />France"), Some(NonUkAddressController.onPageLoad(index, fakeDraftId).url), name, canEdit)
            ),
            headingArgs = Seq(index + 1)
          )
        )
    }
  }
}
