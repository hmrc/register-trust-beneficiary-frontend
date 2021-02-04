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
import controllers.register.beneficiaries.other.routes._
import controllers.register.beneficiaries.other.mld5.{routes => mld5}
import models.core.pages.{InternationalAddress, UKAddress}
import pages.register.beneficiaries.other
import pages.register.beneficiaries.other.{AddressInternationalPage, AddressUKPage, AddressUKYesNoPage, AddressYesNoPage, DescriptionPage}
import pages.register.beneficiaries.other.mld5.{CountryOfResidencePage, CountryOfResidenceYesNoPage, UKResidentYesNoPage}
import play.twirl.api.Html
import utils.countryOptions.CountryOptions
import utils.print.AnswerRowConverter
import viewmodels.{AnswerRow, AnswerSection}

class OtherBeneficiaryAnswersHelperSpec extends SpecBase {

  private val countryOptions: CountryOptions = injector.instanceOf[CountryOptions]
  private val answerRowConverter = injector.instanceOf[AnswerRowConverter]
  private val index: Int = 0
  private val description: String = "Description"
  private val ukAddress: UKAddress = UKAddress("Line 1", "Line 2", Some("Line 3"), Some("Line 4"), "AB11AB")
  private val nonUkAddress: InternationalAddress = InternationalAddress("Line 1", "Line 2", Some("Line 3"), "FR")
  private val nonUkCountry: String = "FR"
  private val canEdit: Boolean = true

  "Other Beneficiary answers helper" must {

    "return a other beneficiary answer section" when {

      "in 4mld journey" when {

        val baseAnswers = emptyUserAnswers.copy(is5mldEnabled = false)

        "minimum data" in {

          val userAnswers = baseAnswers
            .set(other.DescriptionPage(index), description).success.value
            .set(other.IncomeDiscretionYesNoPage(index), true).success.value
            .set(other.AddressYesNoPage(index), false).success.value

          val helper: OtherBeneficiaryAnswersHelper = new OtherBeneficiaryAnswersHelper(answerRowConverter, countryOptions)

          val result = helper.otherBeneficiaries(userAnswers)

          result mustBe Some(Seq(
            AnswerSection(
              headingKey = Some(messages("answerPage.section.otherBeneficiary.subheading", index + 1)),
              rows = Seq(
                AnswerRow(label = "otherBeneficiary.description.checkYourAnswersLabel", answer = Html(description), changeUrl = Some(DescriptionController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = description),
                AnswerRow(label = "otherBeneficiary.discretionYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(DiscretionYesNoController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = description),
                AnswerRow(label = "otherBeneficiary.addressYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(AddressYesNoController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = description)
              ),
              sectionKey = None
            )
          ))
        }

        "full data with international address" in {

          val userAnswers = baseAnswers
            .set(other.DescriptionPage(index), description).success.value
            .set(other.IncomeDiscretionYesNoPage(index), false).success.value
            .set(other.ShareOfIncomePage(index), 10).success.value
            .set(other.AddressYesNoPage(index), true).success.value
            .set(other.AddressUKYesNoPage(index), false).success.value
            .set(other.AddressInternationalPage(index), nonUkAddress).success.value

          val helper: OtherBeneficiaryAnswersHelper = new OtherBeneficiaryAnswersHelper(answerRowConverter, countryOptions)

          val result = helper.otherBeneficiaries(userAnswers)

          result mustBe Some(Seq(
            AnswerSection(
              headingKey = Some(messages("answerPage.section.otherBeneficiary.subheading", index + 1)),
              rows = Seq(
                AnswerRow(label = "otherBeneficiary.description.checkYourAnswersLabel", answer = Html(description), changeUrl = Some(DescriptionController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = description),
                AnswerRow(label = "otherBeneficiary.discretionYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(DiscretionYesNoController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = description),
                AnswerRow(label = "otherBeneficiary.shareOfIncome.checkYourAnswersLabel", answer = Html("10%"), changeUrl = Some(ShareOfIncomeController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = description),
                AnswerRow(label = "otherBeneficiary.addressYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(AddressYesNoController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = description),
                AnswerRow(label = "otherBeneficiary.addressUkYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(AddressUkYesNoController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = description),
                AnswerRow(label = "otherBeneficiary.nonUkAddress.checkYourAnswersLabel", answer = Html("Line 1<br />Line 2<br />Line 3<br />France"), changeUrl = Some(NonUkAddressController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = description)
              ),
              sectionKey = None
            )
          ))
        }
      }

      "in 5mld journey" when {

        val baseAnswers = emptyUserAnswers.copy(is5mldEnabled = true)

        "minimum data" in {

          val userAnswers = baseAnswers
            .set(DescriptionPage(index), description).success.value
            .set(CountryOfResidenceYesNoPage(index), false).success.value
            .set(AddressYesNoPage(index), true).success.value
            .set(AddressUKYesNoPage(index), true).success.value
            .set(AddressUKPage(index), ukAddress).success.value

          val helper: OtherBeneficiaryAnswersHelper = new OtherBeneficiaryAnswersHelper(answerRowConverter, countryOptions)

          val result = helper.otherBeneficiaries(userAnswers)

          result mustBe Some(Seq(
            AnswerSection(
              headingKey = Some(messages("answerPage.section.otherBeneficiary.subheading", index + 1)),
              rows = Seq(
                AnswerRow(label = "otherBeneficiary.description.checkYourAnswersLabel", answer = Html(description), changeUrl = Some(DescriptionController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = description),
                AnswerRow(label = "otherBeneficiary.countryOfResidenceYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(mld5.CountryOfResidenceYesNoController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = description),
                AnswerRow(label = "otherBeneficiary.addressYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(AddressYesNoController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = description),
                AnswerRow(label = "otherBeneficiary.addressUkYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(AddressUkYesNoController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = description),
                AnswerRow(label = "otherBeneficiary.ukAddress.checkYourAnswersLabel", answer = Html("Line 1<br />Line 2<br />Line 3<br />Line 4<br />AB11AB"), changeUrl = Some(UkAddressController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = description)
              ),
              sectionKey = None
            )
          ))
        }

        "full data with international address" in {

          val userAnswers = baseAnswers
            .set(DescriptionPage(index), description).success.value
            .set(CountryOfResidenceYesNoPage(index), true).success.value
            .set(UKResidentYesNoPage(index), false).success.value
            .set(CountryOfResidencePage(index), nonUkCountry).success.value
            .set(AddressYesNoPage(index), true).success.value
            .set(AddressUKYesNoPage(index), true).success.value
            .set(AddressInternationalPage(index), nonUkAddress).success.value

          val helper: OtherBeneficiaryAnswersHelper = new OtherBeneficiaryAnswersHelper(answerRowConverter, countryOptions)

          val result = helper.otherBeneficiaries(userAnswers)

          result mustBe Some(Seq(
            AnswerSection(
              headingKey = Some(messages("answerPage.section.otherBeneficiary.subheading", index + 1)),
              rows = Seq(
                AnswerRow(label = "otherBeneficiary.description.checkYourAnswersLabel", answer = Html(description), changeUrl = Some(DescriptionController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = description),
                AnswerRow(label = "otherBeneficiary.countryOfResidenceYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(mld5.CountryOfResidenceYesNoController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = description),
                AnswerRow(label = "otherBeneficiary.ukResidentYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(mld5.UKResidentYesNoController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = description),
                AnswerRow(label = "otherBeneficiary.countryOfResidence.checkYourAnswersLabel", answer = Html("France"), changeUrl = Some(mld5.CountryOfResidenceController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = description),
                AnswerRow(label = "otherBeneficiary.addressYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(AddressYesNoController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = description),
                AnswerRow(label = "otherBeneficiary.addressUkYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(AddressUkYesNoController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = description),
                AnswerRow(label = "otherBeneficiary.nonUkAddress.checkYourAnswersLabel", answer = Html("Line 1<br />Line 2<br />Line 3<br />France"), changeUrl = Some(NonUkAddressController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = description)
              ),
              sectionKey = None
            )
          ))
        }
      }

    }
  }
}

