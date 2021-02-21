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
import controllers.register.beneficiaries.individualBeneficiary.routes._
import controllers.register.beneficiaries.individualBeneficiary.mld5.routes._
import models.core.pages.{FullName, InternationalAddress, UKAddress}
import models.registration.pages.{PassportOrIdCardDetails, RoleInCompany}
import pages.register.beneficiaries.individual._
import pages.register.beneficiaries.individual.mld5._
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

import java.time.LocalDate

class IndividualBeneficiaryAnswersHelperSpec extends SpecBase {

  private val index: Int = 0
  private val name: FullName = FullName("Joe", None, "Bloggs")
  private val arg = name.toString
  private val date: LocalDate = LocalDate.parse("1996-02-03")
  private val amount: Int = 50
  private val nino: String = "AA000000A"
  private val ukAddress: UKAddress = UKAddress("Line 1", "Line 2", Some("Line 3"), Some("Line 4"), "AB11AB")
  private val country: String = "FR"
  private val nonUkAddress: InternationalAddress = InternationalAddress("Line 1", "Line 2", Some("Line 3"), country)
  private val passportOrIdCard: PassportOrIdCardDetails = PassportOrIdCardDetails(country, "12345", date)
  private val canEdit: Boolean = true

  private val helper: IndividualBeneficiaryAnswersHelper = injector.instanceOf[IndividualBeneficiaryAnswersHelper]

  "Individual beneficiary answers helper" must {

    "return None for empty user answers" in {

      val result = helper.beneficiaries(emptyUserAnswers)

      result mustBe None
    }

    "return an individual beneficiary answer section" in {

      val userAnswers = emptyUserAnswers
        .set(NamePage(index), name).success.value
        .set(RoleInCompanyPage(index), RoleInCompany.Director).success.value
        .set(DateOfBirthYesNoPage(index), true).success.value
        .set(DateOfBirthPage(index), date).success.value
        .set(IncomeYesNoPage(index), false).success.value
        .set(IncomePage(index), amount).success.value
        .set(CountryOfNationalityYesNoPage(index), true).success.value
        .set(CountryOfNationalityInTheUkYesNoPage(index), false).success.value
        .set(CountryOfNationalityPage(index), country).success.value
        .set(NationalInsuranceYesNoPage(index), true).success.value
        .set(NationalInsuranceNumberPage(index), nino).success.value
        .set(CountryOfResidenceYesNoPage(index), true).success.value
        .set(CountryOfResidenceInTheUkYesNoPage(index), false).success.value
        .set(CountryOfResidencePage(index), country).success.value
        .set(AddressYesNoPage(index), true).success.value
        .set(AddressUKYesNoPage(index), true).success.value
        .set(AddressUKPage(index), ukAddress).success.value
        .set(AddressInternationalPage(index), nonUkAddress).success.value
        .set(PassportDetailsYesNoPage(index), true).success.value
        .set(PassportDetailsPage(index), passportOrIdCard).success.value
        .set(IDCardDetailsYesNoPage(index), true).success.value
        .set(IDCardDetailsPage(index), passportOrIdCard).success.value
        .set(MentalCapacityYesNoPage(index), true).success.value
        .set(VulnerableYesNoPage(index), false).success.value

      val result = helper.beneficiaries(userAnswers).get

      result mustBe
        Seq(
          AnswerSection(
            headingKey = Some(messages("answerPage.section.individualBeneficiary.subheading", index + 1)),
            rows = Seq(
              AnswerRow("individualBeneficiaryName.checkYourAnswersLabel", Html(name.displayFullName), Some(NameController.onPageLoad(index, fakeDraftId).url), "", canEdit),
              AnswerRow("individualBeneficiaryRoleInCompany.checkYourAnswersLabel", Html("Director"), Some(RoleInCompanyController.onPageLoad(index, fakeDraftId).url), arg, canEdit),
              AnswerRow("individualBeneficiaryDateOfBirthYesNo.checkYourAnswersLabel", Html("Yes"), Some(DateOfBirthYesNoController.onPageLoad(index, fakeDraftId).url), arg, canEdit),
              AnswerRow("individualBeneficiaryDateOfBirth.checkYourAnswersLabel", Html("3 February 1996"), Some(DateOfBirthController.onPageLoad(index, fakeDraftId).url), arg, canEdit),
              AnswerRow("individualBeneficiaryIncomeYesNo.checkYourAnswersLabel", Html("No"), Some(IncomeYesNoController.onPageLoad(index, fakeDraftId).url), arg, canEdit),
              AnswerRow("individualBeneficiaryIncome.checkYourAnswersLabel", Html("50%"), Some(IncomeController.onPageLoad(index, fakeDraftId).url), arg, canEdit),
              AnswerRow("individualBeneficiary.5mld.countryOfNationalityYesNo.checkYourAnswersLabel", Html("Yes"), Some(CountryOfNationalityYesNoController.onPageLoad(index, fakeDraftId).url), arg, canEdit),
              AnswerRow("individualBeneficiary.5mld.countryOfNationalityInTheUkYesNo.checkYourAnswersLabel", Html("No"), Some(CountryOfNationalityInTheUkYesNoController.onPageLoad(index, fakeDraftId).url), arg, canEdit),
              AnswerRow("individualBeneficiary.5mld.countryOfNationality.checkYourAnswersLabel", Html("France"), Some(CountryOfNationalityController.onPageLoad(index, fakeDraftId).url), arg, canEdit),
              AnswerRow("individualBeneficiaryNationalInsuranceYesNo.checkYourAnswersLabel", Html("Yes"), Some(NationalInsuranceYesNoController.onPageLoad(index, fakeDraftId).url), arg, canEdit),
              AnswerRow("individualBeneficiaryNationalInsuranceNumber.checkYourAnswersLabel", Html("AA 00 00 00 A"), Some(NationalInsuranceNumberController.onPageLoad(index, fakeDraftId).url), arg, canEdit),
              AnswerRow("individualBeneficiary.5mld.countryOfResidenceYesNo.checkYourAnswersLabel", Html("Yes"), Some(CountryOfResidenceYesNoController.onPageLoad(index, fakeDraftId).url), arg, canEdit),
              AnswerRow("individualBeneficiary.5mld.countryOfResidenceInTheUkYesNo.checkYourAnswersLabel", Html("No"), Some(CountryOfResidenceInTheUkYesNoController.onPageLoad(index, fakeDraftId).url), arg, canEdit),
              AnswerRow("individualBeneficiary.5mld.countryOfResidence.checkYourAnswersLabel", Html("France"), Some(CountryOfResidenceController.onPageLoad(index, fakeDraftId).url), arg, canEdit),
              AnswerRow("individualBeneficiaryAddressYesNo.checkYourAnswersLabel", Html("Yes"), Some(AddressYesNoController.onPageLoad(index, fakeDraftId).url), arg, canEdit),
              AnswerRow("individualBeneficiaryAddressUKYesNo.checkYourAnswersLabel", Html("Yes"), Some(AddressUKYesNoController.onPageLoad(index, fakeDraftId).url), arg, canEdit),
              AnswerRow("individualBeneficiaryAddressUK.checkYourAnswersLabel", Html("Line 1<br />Line 2<br />Line 3<br />Line 4<br />AB11AB"), Some(AddressUKController.onPageLoad(index, fakeDraftId).url), arg, canEdit),
              AnswerRow("individualBeneficiaryAddressInternational.checkYourAnswersLabel", Html("Line 1<br />Line 2<br />Line 3<br />France"), Some(AddressInternationalController.onPageLoad(index, fakeDraftId).url), arg, canEdit),
              AnswerRow("individualBeneficiaryPassportDetailsYesNo.checkYourAnswersLabel", Html("Yes"), Some(PassportDetailsYesNoController.onPageLoad(index, fakeDraftId).url), arg, canEdit),
              AnswerRow("individualBeneficiaryPassportDetails.checkYourAnswersLabel", Html("France<br />12345<br />3 February 1996"), Some(PassportDetailsController.onPageLoad(index, fakeDraftId).url), arg, canEdit),
              AnswerRow("individualBeneficiaryIDCardDetailsYesNo.checkYourAnswersLabel", Html("Yes"), Some(IDCardDetailsYesNoController.onPageLoad(index, fakeDraftId).url), arg, canEdit),
              AnswerRow("individualBeneficiaryIDCardDetails.checkYourAnswersLabel", Html("France<br />12345<br />3 February 1996"), Some(IDCardDetailsController.onPageLoad(index, fakeDraftId).url), arg, canEdit),
              AnswerRow("individualBeneficiary.5mld.mentalCapacityYesNo.checkYourAnswersLabel", Html("Yes"), Some(MentalCapacityYesNoController.onPageLoad(index, fakeDraftId).url), arg, canEdit),
              AnswerRow("individualBeneficiaryVulnerableYesNo.checkYourAnswersLabel", Html("No"), Some(VulnerableYesNoController.onPageLoad(index, fakeDraftId).url), arg, canEdit),
            )
          )
        )
    }
  }
}
