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
import controllers.register.beneficiaries.charityortrust.trust.mld5.{routes => ntRts}
import controllers.register.beneficiaries.charityortrust.trust.{routes => rts}
import models.core.pages.{InternationalAddress, UKAddress}
import pages.register.beneficiaries.charityortrust.trust._
import pages.register.beneficiaries.charityortrust.trust.mld5.{CountryOfResidenceInTheUkYesNoPage, CountryOfResidencePage, CountryOfResidenceYesNoPage}
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

class TrustBeneficiaryAnswersHelperSpec extends SpecBase {

  private val index: Int = 0
  private val name: String = "Trust Name"
  private val ukAddress: UKAddress = UKAddress("Line 1", "Line 2", Some("Line 3"), Some("Line 4"), "AB11AB")
  private val nonUkAddress: InternationalAddress = InternationalAddress("Line 1", "Line 2", Some("Line 3"), "FR")
  private val nonUkCountry: String = "FR"
  private val canEdit: Boolean = true

  private val helper: TrustBeneficiaryAnswersHelper = injector.instanceOf[TrustBeneficiaryAnswersHelper]

  "Trust Beneficiary answers helper" must {

    "return a trust beneficiary answer section" when {

      "in taxable journey" when {
        "minimum data" in {

          val userAnswers = emptyUserAnswers
            .set(NamePage(index), name).value
            .set(DiscretionYesNoPage(index), true).value
            .set(CountryOfResidenceYesNoPage(index), false).value
            .set(AddressYesNoPage(index), false).value

          val result = helper.beneficiaries(userAnswers)

          result mustBe Some(Seq(
            AnswerSection(
              headingKey = Some("answerPage.section.trustBeneficiary.subheading"),
              rows = Seq(
                AnswerRow(label = "trustBeneficiaryName.checkYourAnswersLabel", answer = Html(name), changeUrl = Some(rts.NameController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name),
                AnswerRow(label = "trustBeneficiaryDiscretionYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(rts.DiscretionYesNoController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name),
                AnswerRow(label = "trust.5mld.countryOfResidenceYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(ntRts.CountryOfResidenceYesNoController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name),
                AnswerRow(label = "trustBeneficiaryAddressYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(rts.AddressYesNoController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name)
              ),
              headingArgs = Seq(index + 1)
            )
          ))
        }

        "full data with international address" in {

          val userAnswers = emptyUserAnswers
            .set(NamePage(index), name).value
            .set(DiscretionYesNoPage(index), false).value
            .set(ShareOfIncomePage(index), 10).value
            .set(CountryOfResidenceYesNoPage(index), true).value
            .set(CountryOfResidenceInTheUkYesNoPage(index), false).value
            .set(CountryOfResidencePage(index), nonUkCountry).value
            .set(AddressYesNoPage(index), true).value
            .set(AddressUKYesNoPage(index), false).value
            .set(AddressInternationalPage(index), nonUkAddress).value

          val result = helper.beneficiaries(userAnswers)

          result mustBe Some(Seq(
            AnswerSection(
              headingKey = Some("answerPage.section.trustBeneficiary.subheading"),
              rows = Seq(
                AnswerRow(label = "trustBeneficiaryName.checkYourAnswersLabel", answer = Html(name), changeUrl = Some(rts.NameController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name),
                AnswerRow(label = "trustBeneficiaryDiscretionYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(rts.DiscretionYesNoController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name),
                AnswerRow(label = "trustBeneficiaryShareOfIncome.checkYourAnswersLabel", answer = Html("10%"), changeUrl = Some(rts.ShareOfIncomeController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name),
                AnswerRow(label = "trust.5mld.countryOfResidenceYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(ntRts.CountryOfResidenceYesNoController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name),
                AnswerRow(label = "trust.5mld.countryOfResidenceInTheUkYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(ntRts.CountryOfResidenceInTheUkYesNoController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name),
                AnswerRow(label = "trust.5mld.countryOfResidence.checkYourAnswersLabel", answer = Html("France"), changeUrl = Some(ntRts.CountryOfResidenceController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name),
                AnswerRow(label = "trustBeneficiaryAddressYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(rts.AddressYesNoController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name),
                AnswerRow(label = "trustBeneficiaryAddressUKYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(rts.AddressUKYesNoController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name),
                AnswerRow(label = "site.address.international.checkYourAnswersLabel", answer = Html("Line 1<br />Line 2<br />Line 3<br />France"), changeUrl = Some(rts.AddressInternationalController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name)
              ),
              headingArgs = Seq(index + 1)
            )
          ))
        }

        "full data with UK address" in {

          val userAnswers = emptyUserAnswers
            .set(NamePage(index), name).value
            .set(DiscretionYesNoPage(index), false).value
            .set(ShareOfIncomePage(index), 10).value
            .set(CountryOfResidenceYesNoPage(index), true).value
            .set(CountryOfResidenceInTheUkYesNoPage(index), true).value
            .set(AddressYesNoPage(index), true).value
            .set(AddressUKYesNoPage(index), true).value
            .set(AddressUKPage(index), ukAddress).value

          val result = helper.beneficiaries(userAnswers)

          result mustBe Some(Seq(
            AnswerSection(
              headingKey = Some("answerPage.section.trustBeneficiary.subheading"),
              rows = Seq(
                AnswerRow(label = "trustBeneficiaryName.checkYourAnswersLabel", answer = Html(name), changeUrl = Some(rts.NameController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name),
                AnswerRow(label = "trustBeneficiaryDiscretionYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(rts.DiscretionYesNoController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name),
                AnswerRow(label = "trustBeneficiaryShareOfIncome.checkYourAnswersLabel", answer = Html("10%"), changeUrl = Some(rts.ShareOfIncomeController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name),
                AnswerRow(label = "trust.5mld.countryOfResidenceYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(ntRts.CountryOfResidenceYesNoController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name),
                AnswerRow(label = "trust.5mld.countryOfResidenceInTheUkYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(ntRts.CountryOfResidenceInTheUkYesNoController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name),
                AnswerRow(label = "trustBeneficiaryAddressYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(rts.AddressYesNoController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name),
                AnswerRow(label = "trustBeneficiaryAddressUKYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(rts.AddressUKYesNoController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name),
                AnswerRow(label = "site.address.uk.checkYourAnswersLabel", answer = Html("Line 1<br />Line 2<br />Line 3<br />Line 4<br />AB11AB"), changeUrl = Some(rts.AddressUKController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name)
              ),
              headingArgs = Seq(index + 1)
            )
          ))
        }
      }

      "in non taxable journey" when {
        "minimum data" in {

          val userAnswers = emptyUserAnswers
            .set(NamePage(index), name).value
            .set(CountryOfResidenceYesNoPage(index), false).value

          val result = helper.beneficiaries(userAnswers)

          result mustBe Some(Seq(
            AnswerSection(
              headingKey = Some("answerPage.section.trustBeneficiary.subheading"),
              rows = Seq(
                AnswerRow(label = "trustBeneficiaryName.checkYourAnswersLabel", answer = Html(name), changeUrl = Some(rts.NameController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name),
                AnswerRow(label = "trust.5mld.countryOfResidenceYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(ntRts.CountryOfResidenceYesNoController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name)
              ),
              headingArgs = Seq(index + 1)
            )
          ))
        }

        "maximum data" in {

          val userAnswers = emptyUserAnswers
            .set(NamePage(index), name).value
            .set(CountryOfResidenceYesNoPage(index), true).value
            .set(CountryOfResidenceInTheUkYesNoPage(index), false).value
            .set(CountryOfResidencePage(index), nonUkCountry).value

          val result = helper.beneficiaries(userAnswers)

          result mustBe Some(Seq(
            AnswerSection(
              headingKey = Some("answerPage.section.trustBeneficiary.subheading"),
              rows = Seq(
                AnswerRow(label = "trustBeneficiaryName.checkYourAnswersLabel", answer = Html(name), changeUrl = Some(rts.NameController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name),
                AnswerRow(label = "trust.5mld.countryOfResidenceYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(ntRts.CountryOfResidenceYesNoController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name),
                AnswerRow(label = "trust.5mld.countryOfResidenceInTheUkYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(ntRts.CountryOfResidenceInTheUkYesNoController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name),
                AnswerRow(label = "trust.5mld.countryOfResidence.checkYourAnswersLabel", answer = Html("France"), changeUrl = Some(ntRts.CountryOfResidenceController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name)
              ),
              headingArgs = Seq(index + 1)
            )
          ))
        }
      }
    }
  }
}
