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
              .set(NamePage(index0), FullName("first name", None, "last name")).value
              .set(DateOfBirthYesNoPage(index0), true).value
              .set(DateOfBirthPage(index0), dateOfBirth).value
              .set(IncomeYesNoPage(index0), false).value
              .set(IncomePage(index0), 100).value
              .set(NationalInsuranceYesNoPage(index0), true).value
              .set(NationalInsuranceNumberPage(index0), "AB123456C").value
              .set(VulnerableYesNoPage(index0), true).value

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
              .set(NamePage(index0), FullName("first name", None, "last name")).value
              .set(DateOfBirthYesNoPage(index0), true).value
              .set(DateOfBirthPage(index0), dateOfBirth).value
              .set(IncomeYesNoPage(index0), false).value
              .set(IncomePage(index0), 100).value
              .set(RoleInCompanyPage(index0), Director).value
              .set(VulnerableYesNoPage(index0), true).value

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
              .set(NamePage(index0), FullName("first name", None, "last name")).value
              .set(DateOfBirthYesNoPage(index0), true).value
              .set(DateOfBirthPage(index0), dateOfBirth).value
              .set(IncomeYesNoPage(index0), true).value
              .set(NationalInsuranceYesNoPage(index0), false).value
              .set(VulnerableYesNoPage(index0), false).value
              .set(AddressYesNoPage(index0), true).value
              .set(AddressUKYesNoPage(index0), true).value
              .set(AddressUKPage(index0),
                UKAddress("Line1", "Line2", None, Some("Newcastle"), "NE62RT")).value

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
              .set(NamePage(index0), FullName("first name", None, "last name")).value
              .set(DateOfBirthYesNoPage(index0), false).value
              .set(IncomeYesNoPage(index0), true).value
              .set(NationalInsuranceYesNoPage(index0), false).value
              .set(AddressYesNoPage(index0), false).value
              .set(VulnerableYesNoPage(index0), false).value

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
              .set(NamePage(index0), FullName("first name", None, "last name")).value
              .set(DateOfBirthYesNoPage(index0), false).value
              .set(IncomeYesNoPage(index0), true).value
              .set(NationalInsuranceYesNoPage(index0), false).value
              .set(AddressYesNoPage(index0), false).value
              .set(VulnerableYesNoPage(index0), false).value
              .set(PassportDetailsYesNoPage(index0), true).value
              .set(PassportDetailsPage(index0), PassportOrIdCardDetails(GB, "012345678", LocalDate.of(2024, 5, 13))).value

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
              .set(NamePage(index0), FullName("first name", None, "last name")).value
              .set(DateOfBirthYesNoPage(index0), false).value
              .set(IncomeYesNoPage(index0), true).value
              .set(NationalInsuranceYesNoPage(index0), false).value
              .set(AddressYesNoPage(index0), false).value
              .set(VulnerableYesNoPage(index0), false).value
              .set(PassportDetailsYesNoPage(index0), false).value
              .set(IDCardDetailsYesNoPage(index0), true).value
              .set(IDCardDetailsPage(index0), PassportOrIdCardDetails(GB, "012345678", LocalDate.of(2024, 5, 13))).value

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
            .set(NamePage(index0), FullName("first name", None, "last name")).value
            .set(DateOfBirthYesNoPage(index0), true).value
            .set(DateOfBirthPage(index0), dateOfBirth).value
            .set(IncomeYesNoPage(index0), false).value
            .set(IncomePage(index0), 100).value
            .set(NationalInsuranceYesNoPage(index0), true).value
            .set(NationalInsuranceNumberPage(index0), "AB123456C").value
            .set(VulnerableYesNoPage(index0), true).value

            .set(NamePage(index1), FullName("first name", None, "last name")).value
            .set(DateOfBirthYesNoPage(index1), true).value
            .set(DateOfBirthPage(index1), dateOfBirth).value
            .set(IncomeYesNoPage(index1), false).value
            .set(IncomePage(index1), 100).value
            .set(NationalInsuranceYesNoPage(index1), false).value
            .set(VulnerableYesNoPage(index1), false).value
            .set(AddressYesNoPage(index1), true).value
            .set(AddressUKYesNoPage(index1), true).value
            .set(AddressUKPage(index1),
              UKAddress("line1", "line2", None, None, "NE62RT")).value


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
            .set(NamePage(index0), FullName("first name", None, "last name")).value
            .set(DateOfBirthYesNoPage(index0), true).value
            .set(DateOfBirthPage(index0), dateOfBirth).value
            .set(IncomeYesNoPage(index0), false).value
            .set(IncomePage(index0), 100).value
            .set(NationalInsuranceYesNoPage(index0), true).value
            .set(NationalInsuranceNumberPage(index0), "AB123456C").value
            .set(VulnerableYesNoPage(index0), true).value
            .set(CountryOfNationalityYesNoPage(index0), true).value
            .set(CountryOfNationalityInTheUkYesNoPage(index0), true).value
            .set(CountryOfResidenceYesNoPage(index0), true).value
            .set(CountryOfResidenceInTheUkYesNoPage(index0), true).value
            .set(MentalCapacityYesNoPage(index0), YesNoDontKnow.Yes).value

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
            .set(NamePage(index0), FullName("first name", None, "last name")).value
            .set(DateOfBirthYesNoPage(index0), true).value
            .set(DateOfBirthPage(index0), dateOfBirth).value
            .set(IncomeYesNoPage(index0), false).value
            .set(IncomePage(index0), 100).value
            .set(NationalInsuranceYesNoPage(index0), true).value
            .set(NationalInsuranceNumberPage(index0), "AB123456C").value
            .set(VulnerableYesNoPage(index0), true).value
            .set(CountryOfNationalityYesNoPage(index0), true).value
            .set(CountryOfNationalityInTheUkYesNoPage(index0), false).value
            .set(CountryOfNationalityPage(index0), ES).value
            .set(CountryOfResidenceYesNoPage(index0), true).value
            .set(CountryOfResidenceInTheUkYesNoPage(index0), false).value
            .set(CountryOfResidencePage(index0), ES).value
            .set(MentalCapacityYesNoPage(index0), YesNoDontKnow.No).value

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
            .set(NamePage(index0), FullName("first name", None, "last name")).value
            .set(IncomeYesNoPage(index0), true).value
            .set(NationalInsuranceYesNoPage(index0), true).value
            .set(NationalInsuranceNumberPage(index0), "AB123456C").value
            .set(VulnerableYesNoPage(index0), true).value
            .set(CountryOfNationalityYesNoPage(index0), true).value
            .set(CountryOfNationalityInTheUkYesNoPage(index0), false).value
            .set(CountryOfNationalityPage(index0), ES).value
            .set(CountryOfResidenceYesNoPage(index0), true).value
            .set(CountryOfResidenceInTheUkYesNoPage(index0), false).value
            .set(CountryOfResidencePage(index0), ES).value
            .set(MentalCapacityYesNoPage(index0), YesNoDontKnow.DontKnow).value

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
            .set(NamePage(index0), FullName("first name", None, "last name")).value
            .set(DateOfBirthYesNoPage(index0), true).value
            .set(DateOfBirthPage(index0), dateOfBirth).value
            .set(CountryOfNationalityYesNoPage(index0), true).value
            .set(CountryOfNationalityInTheUkYesNoPage(index0), true).value
            .set(CountryOfResidenceYesNoPage(index0), true).value
            .set(CountryOfResidenceInTheUkYesNoPage(index0), true).value
            .set(MentalCapacityYesNoPage(index0), YesNoDontKnow.Yes).value

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
