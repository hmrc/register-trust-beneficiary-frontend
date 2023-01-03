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

package mapping.registration

import base.SpecBase
import generators.Generators
import models.core.pages.{FullName, UKAddress}
import models.registration.pages.PassportOrIdCardDetails
import models.registration.pages.RoleInCompany.Director
import models.{AddressType, IdentificationType, IndividualDetailsType, PassportType, YesNoDontKnow}
import org.scalatest.OptionValues
import pages.register.beneficiaries.individual._
import pages.register.beneficiaries.individual.mld5._
import utils.Constants._

import java.time.LocalDate

class IndividualBeneficiaryMapperSpec extends SpecBase
  with OptionValues with Generators {

  private val individualBeneficiariesMapper: IndividualBeneficiaryMapper = injector.instanceOf[IndividualBeneficiaryMapper]
  private val index0 = 0
  private val index1 = 1
  private val dateOfBirth = LocalDate.of(2010, 10, 10)

  "IndividualBeneficiariesMapper" when {

    "when user answers is empty" must {

      "must not be able to create IndividualDetailsType" in {

        val userAnswers = emptyUserAnswers

        individualBeneficiariesMapper.build(userAnswers) mustNot be(defined)
      }
    }

    "when user answers is not empty" must {

      "must be able to create IndividualDetailsType" when {

        "Nino is set" in {

          val userAnswers =
            emptyUserAnswers
              .set(NamePage(index0), FullName("first name", None, "last name")).right.get
              .set(DateOfBirthYesNoPage(index0), true).right.get
              .set(DateOfBirthPage(index0), dateOfBirth).right.get
              .set(IncomeYesNoPage(index0), false).right.get
              .set(IncomePage(index0), 100).right.get
              .set(NationalInsuranceYesNoPage(index0), true).right.get
              .set(NationalInsuranceNumberPage(index0), "AB123456C").right.get
              .set(VulnerableYesNoPage(index0), true).right.get

          val individuals = individualBeneficiariesMapper.build(userAnswers)

          individuals mustBe defined
          individuals.value.head mustBe IndividualDetailsType(
            name = FullName("first name", None, "last name"),
            dateOfBirth = Some(dateOfBirth),
            vulnerableBeneficiary = Some(true),
            beneficiaryType = None,
            beneficiaryDiscretion = Some(false),
            beneficiaryShareOfIncome = Some("100"),
            identification = Some(IdentificationType(nino = Some("AB123456C"), None, None)),
            countryOfResidence = None,
            nationality = None,
            legallyIncapable = None
          )
        }

        "Role In Company is set" in {
          val userAnswers =
            emptyUserAnswers
              .set(NamePage(index0), FullName("first name", None, "last name")).right.get
              .set(DateOfBirthYesNoPage(index0), true).right.get
              .set(DateOfBirthPage(index0), dateOfBirth).right.get
              .set(IncomeYesNoPage(index0), false).right.get
              .set(IncomePage(index0), 100).right.get
              .set(RoleInCompanyPage(index0), Director).right.get
              .set(VulnerableYesNoPage(index0), true).right.get

          val individuals = individualBeneficiariesMapper.build(userAnswers)

          individuals mustBe defined
          individuals.value.head mustBe IndividualDetailsType(
            name = FullName("first name", None, "last name"),
            dateOfBirth = Some(dateOfBirth),
            vulnerableBeneficiary = Some(true),
            beneficiaryType = Some(Director.toString),
            beneficiaryDiscretion = Some(false),
            beneficiaryShareOfIncome = Some("100"),
            identification = None,
            countryOfResidence = None,
            nationality = None,
            legallyIncapable = None
          )
        }

        "UK Address is set" in {
          val userAnswers =
            emptyUserAnswers
              .set(NamePage(index0), FullName("first name", None, "last name")).right.get
              .set(DateOfBirthYesNoPage(index0), true).right.get
              .set(DateOfBirthPage(index0), dateOfBirth).right.get
              .set(IncomeYesNoPage(index0), true).right.get
              .set(NationalInsuranceYesNoPage(index0), false).right.get
              .set(VulnerableYesNoPage(index0), false).right.get
              .set(AddressYesNoPage(index0), true).right.get
              .set(AddressUKYesNoPage(index0), true).right.get
              .set(AddressUKPage(index0),
                UKAddress("Line1", "Line2", None, Some("Newcastle"), "NE62RT")).right.get

          val individuals = individualBeneficiariesMapper.build(userAnswers)

          individuals mustBe defined
          individuals.value.head mustBe IndividualDetailsType(
            name = FullName("first name", None, "last name"),
            dateOfBirth = Some(dateOfBirth),
            vulnerableBeneficiary = Some(false),
            beneficiaryType = None,
            beneficiaryDiscretion = Some(true),
            beneficiaryShareOfIncome = None,
            identification = Some(IdentificationType(
              nino = None,
              None,
              address = Some(
                AddressType("Line1", "Line2", None, Some("Newcastle"), Some("NE62RT"), GB)
              )
            )),
            countryOfResidence = None,
            nationality = None,
            legallyIncapable = None
          )
        }

        "Nino And Address are not set" in {
          val userAnswers =
            emptyUserAnswers
              .set(NamePage(index0), FullName("first name", None, "last name")).right.get
              .set(DateOfBirthYesNoPage(index0), false).right.get
              .set(IncomeYesNoPage(index0), true).right.get
              .set(NationalInsuranceYesNoPage(index0), false).right.get
              .set(AddressYesNoPage(index0), false).right.get
              .set(VulnerableYesNoPage(index0), false).right.get

          val individuals = individualBeneficiariesMapper.build(userAnswers)

          individuals mustBe defined
          individuals.value.head mustBe IndividualDetailsType(
            name = FullName("first name", None, "last name"),
            dateOfBirth = None,
            vulnerableBeneficiary = Some(false),
            beneficiaryType = None,
            beneficiaryDiscretion = Some(true),
            beneficiaryShareOfIncome = None,
            identification = None,
            countryOfResidence = None,
            nationality = None,
            legallyIncapable = None
          )
        }

        "Passport is set" in {
          val userAnswers =
            emptyUserAnswers
              .set(NamePage(index0), FullName("first name", None, "last name")).right.get
              .set(DateOfBirthYesNoPage(index0), false).right.get
              .set(IncomeYesNoPage(index0), true).right.get
              .set(NationalInsuranceYesNoPage(index0), false).right.get
              .set(AddressYesNoPage(index0), false).right.get
              .set(VulnerableYesNoPage(index0), false).right.get
              .set(PassportDetailsYesNoPage(index0), true).right.get
              .set(PassportDetailsPage(index0), PassportOrIdCardDetails(GB, "012345678", LocalDate.of(2024, 5, 13))).right.get

          val individuals = individualBeneficiariesMapper.build(userAnswers)

          individuals mustBe defined
          individuals.value.head mustBe IndividualDetailsType(
            name = FullName("first name", None, "last name"),
            dateOfBirth = None,
            vulnerableBeneficiary = Some(false),
            beneficiaryType = None,
            beneficiaryDiscretion = Some(true),
            beneficiaryShareOfIncome = None,
            identification = Some(IdentificationType(
              None,
              Some(PassportType("012345678", LocalDate.of(2024, 5, 13), GB)),
              None
            )),
            countryOfResidence = None,
            nationality = None,
            legallyIncapable = None
          )
        }
        "Id card is set" in {
          val userAnswers =
            emptyUserAnswers
              .set(NamePage(index0), FullName("first name", None, "last name")).right.get
              .set(DateOfBirthYesNoPage(index0), false).right.get
              .set(IncomeYesNoPage(index0), true).right.get
              .set(NationalInsuranceYesNoPage(index0), false).right.get
              .set(AddressYesNoPage(index0), false).right.get
              .set(VulnerableYesNoPage(index0), false).right.get
              .set(PassportDetailsYesNoPage(index0), false).right.get
              .set(IDCardDetailsYesNoPage(index0), true).right.get
              .set(IDCardDetailsPage(index0), PassportOrIdCardDetails(GB, "012345678", LocalDate.of(2024, 5, 13))).right.get

          val individuals = individualBeneficiariesMapper.build(userAnswers)

          individuals mustBe defined
          individuals.value.head mustBe IndividualDetailsType(
            name = FullName("first name", None, "last name"),
            dateOfBirth = None,
            vulnerableBeneficiary = Some(false),
            beneficiaryType = None,
            beneficiaryDiscretion = Some(true),
            beneficiaryShareOfIncome = None,
            identification = Some(IdentificationType(
              None,
              Some(PassportType("012345678", LocalDate.of(2024, 5, 13), GB)),
              None
            )),
            countryOfResidence = None,
            nationality = None,
            legallyIncapable = None
          )
        }
      }

      "must be able to create multiple IndividualDetailsType, first with Nino and second with UKAddress" in {
        val userAnswers =
          emptyUserAnswers
            .set(NamePage(index0), FullName("first name", None, "last name")).right.get
            .set(DateOfBirthYesNoPage(index0), true).right.get
            .set(DateOfBirthPage(index0), dateOfBirth).right.get
            .set(IncomeYesNoPage(index0), false).right.get
            .set(IncomePage(index0), 100).right.get
            .set(NationalInsuranceYesNoPage(index0), true).right.get
            .set(NationalInsuranceNumberPage(index0), "AB123456C").right.get
            .set(VulnerableYesNoPage(index0), true).right.get

            .set(NamePage(index1), FullName("first name", None, "last name")).right.get
            .set(DateOfBirthYesNoPage(index1), true).right.get
            .set(DateOfBirthPage(index1), dateOfBirth).right.get
            .set(IncomeYesNoPage(index1), false).right.get
            .set(IncomePage(index1), 100).right.get
            .set(NationalInsuranceYesNoPage(index1), false).right.get
            .set(VulnerableYesNoPage(index1), false).right.get
            .set(AddressYesNoPage(index1), true).right.get
            .set(AddressUKYesNoPage(index1), true).right.get
            .set(AddressUKPage(index1),
              UKAddress("line1", "line2", None, None, "NE62RT")).right.get


        val individuals = individualBeneficiariesMapper.build(userAnswers)

        individuals mustBe defined
        individuals.value mustBe
          List(
            IndividualDetailsType(
              name = FullName("first name", None, "last name"),
              dateOfBirth = Some(dateOfBirth),
              vulnerableBeneficiary = Some(true),
              beneficiaryType = None,
              beneficiaryDiscretion = Some(false),
              beneficiaryShareOfIncome = Some("100"),
              identification = Some(
                IdentificationType(
                  nino = Some("AB123456C"),
                  passport = None,
                  address = None)
              ),
              countryOfResidence = None,
              nationality = None,
              legallyIncapable = None),

            IndividualDetailsType(
              name = FullName("first name", None, "last name"),
              dateOfBirth = Some(dateOfBirth),
              vulnerableBeneficiary = Some(false),
              beneficiaryType = None,
              beneficiaryDiscretion = Some(false),
              beneficiaryShareOfIncome = Some("100"),
              identification = Some(
                IdentificationType(
                  nino = None,
                  passport = None,
                  address = Some(
                    AddressType("line1", "line2", None, None, Some("NE62RT"), GB)
                  ))
              ),
              countryOfResidence = None,
              nationality = None,
              legallyIncapable = None)
          )
      }

      "In taxable mode" when {
        val baseAnswers = emptyUserAnswers.copy(isTaxable = true)
        "must not be able to create IndividualDetailsType when incomplete data " in {
          individualBeneficiariesMapper.build(baseAnswers) mustNot be(defined)
        }

        "with UK country of residence, UK country of nationality and Mental Capacity" in {
          val userAnswers = baseAnswers
            .set(NamePage(index0), FullName("first name", None, "last name")).right.get
            .set(DateOfBirthYesNoPage(index0), true).right.get
            .set(DateOfBirthPage(index0), dateOfBirth).right.get
            .set(IncomeYesNoPage(index0), false).right.get
            .set(IncomePage(index0), 100).right.get
            .set(NationalInsuranceYesNoPage(index0), true).right.get
            .set(NationalInsuranceNumberPage(index0), "AB123456C").right.get
            .set(VulnerableYesNoPage(index0), true).right.get
            .set(CountryOfNationalityYesNoPage(index0), true).right.get
            .set(CountryOfNationalityInTheUkYesNoPage(index0), true).right.get
            .set(CountryOfResidenceYesNoPage(index0), true).right.get
            .set(CountryOfResidenceInTheUkYesNoPage(index0), true).right.get
            .set(MentalCapacityYesNoPage(index0), YesNoDontKnow.Yes).right.get

          val individuals = individualBeneficiariesMapper.build(userAnswers)

          individuals mustBe defined
          individuals.value.head mustBe IndividualDetailsType(
            name = FullName("first name", None, "last name"),
            dateOfBirth = Some(dateOfBirth),
            vulnerableBeneficiary = Some(true),
            beneficiaryType = None,
            beneficiaryDiscretion = Some(false),
            beneficiaryShareOfIncome = Some("100"),
            identification = Some(IdentificationType(nino = Some("AB123456C"), None, None)),
            countryOfResidence = Some(GB),
            nationality = Some(GB),
            legallyIncapable = Some(false)
          )

        }

        "with Non UK country of residence, Non UK country of nationality and No Mental Capacity" in {

          val userAnswers = baseAnswers
            .set(NamePage(index0), FullName("first name", None, "last name")).right.get
            .set(DateOfBirthYesNoPage(index0), true).right.get
            .set(DateOfBirthPage(index0), dateOfBirth).right.get
            .set(IncomeYesNoPage(index0), false).right.get
            .set(IncomePage(index0), 100).right.get
            .set(NationalInsuranceYesNoPage(index0), true).right.get
            .set(NationalInsuranceNumberPage(index0), "AB123456C").right.get
            .set(VulnerableYesNoPage(index0), true).right.get
            .set(CountryOfNationalityYesNoPage(index0), true).right.get
            .set(CountryOfNationalityInTheUkYesNoPage(index0), false).right.get
            .set(CountryOfNationalityPage(index0), ES).right.get
            .set(CountryOfResidenceYesNoPage(index0), true).right.get
            .set(CountryOfResidenceInTheUkYesNoPage(index0), false).right.get
            .set(CountryOfResidencePage(index0), ES).right.get
            .set(MentalCapacityYesNoPage(index0), YesNoDontKnow.No).right.get

          val individuals = individualBeneficiariesMapper.build(userAnswers)

          individuals mustBe defined
          individuals.value.head mustBe IndividualDetailsType(
            name = FullName("first name", None, "last name"),
            dateOfBirth = Some(dateOfBirth),
            vulnerableBeneficiary = Some(true),
            beneficiaryType = None,
            beneficiaryDiscretion = Some(false),
            beneficiaryShareOfIncome = Some("100"),
            identification = Some(IdentificationType(nino = Some("AB123456C"), None, None)),
            countryOfResidence = Some(ES),
            nationality = Some(ES),
            legallyIncapable = Some(true)
          )

        }

        "with Don't know Mental Capacity" in {

          val userAnswers = baseAnswers
            .set(NamePage(index0), FullName("first name", None, "last name")).right.get
            .set(IncomeYesNoPage(index0), true).right.get
            .set(NationalInsuranceYesNoPage(index0), true).right.get
            .set(NationalInsuranceNumberPage(index0), "AB123456C").right.get
            .set(VulnerableYesNoPage(index0), true).right.get
            .set(CountryOfNationalityYesNoPage(index0), true).right.get
            .set(CountryOfNationalityInTheUkYesNoPage(index0), false).right.get
            .set(CountryOfNationalityPage(index0), ES).right.get
            .set(CountryOfResidenceYesNoPage(index0), true).right.get
            .set(CountryOfResidenceInTheUkYesNoPage(index0), false).right.get
            .set(CountryOfResidencePage(index0), ES).right.get
            .set(MentalCapacityYesNoPage(index0), YesNoDontKnow.DontKnow).right.get

          val individuals = individualBeneficiariesMapper.build(userAnswers)

          individuals mustBe defined
          individuals.value.head mustBe IndividualDetailsType(
            name = FullName("first name", None, "last name"),
            dateOfBirth = None,
            vulnerableBeneficiary = Some(true),
            beneficiaryType = None,
            beneficiaryDiscretion = Some(true),
            beneficiaryShareOfIncome = None,
            identification = Some(IdentificationType(nino = Some("AB123456C"), None, None)),
            countryOfResidence = Some(ES),
            nationality = Some(ES),
            legallyIncapable = None
          )

        }
      }

      "In none taxable mode" when {
        val baseAnswers = emptyUserAnswers.copy(isTaxable = false)

        "with No Income or Vulnerable data answered" in {
          val userAnswers = baseAnswers
            .set(NamePage(index0), FullName("first name", None, "last name")).right.get
            .set(DateOfBirthYesNoPage(index0), true).right.get
            .set(DateOfBirthPage(index0), dateOfBirth).right.get
            .set(CountryOfNationalityYesNoPage(index0), true).right.get
            .set(CountryOfNationalityInTheUkYesNoPage(index0), true).right.get
            .set(CountryOfResidenceYesNoPage(index0), true).right.get
            .set(CountryOfResidenceInTheUkYesNoPage(index0), true).right.get
            .set(MentalCapacityYesNoPage(index0), YesNoDontKnow.Yes).right.get

          val individuals = individualBeneficiariesMapper.build(userAnswers)

          individuals mustBe defined
          individuals.value.head mustBe IndividualDetailsType(
            name = FullName("first name", None, "last name"),
            dateOfBirth = Some(dateOfBirth),
            vulnerableBeneficiary = None,
            beneficiaryType = None,
            beneficiaryDiscretion = None,
            beneficiaryShareOfIncome = None,
            identification = None,
            countryOfResidence = Some(GB),
            nationality = Some(GB),
            legallyIncapable = Some(false)
          )

        }
      }
    }
  }
}
