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

package mapping.registration

import base.SpecBase
import generators.Generators
import models.core.pages.{InternationalAddress, UKAddress}
import org.scalatest.{MustMatchers, OptionValues}
import pages.register.beneficiaries.companyoremploymentrelated.company._
import pages.register.beneficiaries.companyoremploymentrelated.company.mld5._

class CompanyBeneficiaryMapperSpec extends SpecBase with MustMatchers with OptionValues with Generators {

  private val mapper = injector.instanceOf[CompanyBeneficiaryMapper]
  private val index0 = 0
  private val index1 = 1

  "CompanyBeneficiariesMapper" when {

    "when user answers is empty" must {

      "must return None" in {

        val userAnswers = emptyUserAnswers

        mapper.build(userAnswers) mustNot be(defined)
      }
    }

    "when user answers is not empty" must {

      "return mapped data" when {

        "No address is set" in {
          val userAnswers =
            emptyUserAnswers
              .set(NamePage(index0), "Company Name").success.value
              .set(IncomeYesNoPage(index0), true).success.value
              .set(AddressYesNoPage(index0), false).success.value

          val companies = mapper.build(userAnswers)

          companies mustBe defined
          companies.value.head mustBe CompanyType(
            organisationName = "Company Name",
            beneficiaryDiscretion = Some(true),
            beneficiaryShareOfIncome = None,
            identification = None,
            countryOfResidence = None
          )
        }

        "Income value is set" in {
          val userAnswers =
            emptyUserAnswers
              .set(NamePage(index0), "Company Name").success.value
              .set(IncomeYesNoPage(index0), false).success.value
              .set(IncomePage(index0), 42).success.value
              .set(AddressYesNoPage(index0), false).success.value

          val companies = mapper.build(userAnswers)

          companies mustBe defined
          companies.value.head mustBe CompanyType(
            organisationName = "Company Name",
            beneficiaryDiscretion = Some(false),
            beneficiaryShareOfIncome = Some("42"),
            identification = None,
            countryOfResidence = None
          )
        }


        "UK Address is set" in {
          val userAnswers =
            emptyUserAnswers
              .set(NamePage(index0), "Company Name").success.value
              .set(IncomeYesNoPage(index0), true).success.value
              .set(AddressYesNoPage(index0), true).success.value
              .set(AddressUKYesNoPage(index0), true).success.value
              .set(AddressUKPage(index0),
                UKAddress("Line1", "Line2", Some("Line3"), Some("Newcastle"), "NE62RT")).success.value

          val companies = mapper.build(userAnswers)

          companies mustBe defined
          companies.value.head mustBe CompanyType(
            organisationName = "Company Name",
            beneficiaryDiscretion = Some(true),
            beneficiaryShareOfIncome = None,
            identification = Some(IdentificationOrgType(
              None,
              address = Some(
                AddressType("Line1", "Line2", Some("Line3"), Some("Newcastle"), Some("NE62RT"), "GB")
              ))),
            countryOfResidence = None
          )
        }
        "International Address is set" in {
          val userAnswers =
            emptyUserAnswers
              .set(NamePage(index0), "Company Name").success.value
              .set(IncomeYesNoPage(index0), true).success.value
              .set(AddressYesNoPage(index0), true).success.value
              .set(AddressUKYesNoPage(index0), false).success.value
              .set(AddressInternationalPage(index0),
                InternationalAddress("Line1", "Line2", Some("Line3"), "US")).success.value

          val companies = mapper.build(userAnswers)

          companies mustBe defined
          companies.value.head mustBe CompanyType(
            organisationName = "Company Name",
            beneficiaryDiscretion = Some(true),
            beneficiaryShareOfIncome = None,
            identification = Some(IdentificationOrgType(
              None,
              address = Some(
                AddressType("Line1", "Line2", Some("Line3"), None, None, "US")
              )
            )),
            countryOfResidence = None
          )
        }

      }
      "must be able to create multiple Company beneficiaries" in {
        val userAnswers =
          emptyUserAnswers
            .set(NamePage(index0), "Company 1").success.value
            .set(IncomeYesNoPage(index0), false).success.value
            .set(IncomePage(index0), 100).success.value

            .set(NamePage(index1), "Company 2").success.value
            .set(IncomeYesNoPage(index1), true).success.value
            .set(AddressYesNoPage(index1), true).success.value
            .set(AddressUKYesNoPage(index1), true).success.value
            .set(AddressUKPage(index1),
              UKAddress("line1", "line2", None, None, "NE62RT")).success.value


        val individuals = mapper.build(userAnswers)

        individuals mustBe defined
        individuals.value mustBe
          List(
            CompanyType(
              organisationName = "Company 1",
              beneficiaryDiscretion = Some(false),
              beneficiaryShareOfIncome = Some("100"),
              identification = None,
              countryOfResidence = None),

            CompanyType(
              organisationName = "Company 2",
              beneficiaryDiscretion = Some(true),
              beneficiaryShareOfIncome = None,
              identification = Some(
                IdentificationOrgType(
                  None,
                  address = Some(
                    AddressType("line1", "line2", None, None, Some("NE62RT"), "GB")
                  ))),
              countryOfResidence = None
            )
          )
      }


      "Country of residence is set to the UK in 5mld mode" in {
        val userAnswers =
          emptyUserAnswers
            .set(NamePage(index0), "Company Name").success.value
            .set(IncomeYesNoPage(index0), false).success.value
            .set(IncomePage(index0),60).success.value
            .set(CountryOfResidenceYesNoPage(index0), true).success.value
            .set(CountryOfResidenceInTheUkYesNoPage(index0), true).success.value
            .set(AddressYesNoPage(index0), false).success.value

        val company = mapper.build(userAnswers)

        company mustBe defined
        company.value.head mustBe CompanyType(
          organisationName = "Company Name",
          beneficiaryDiscretion = Some(false),
          beneficiaryShareOfIncome = Some("60"),
          identification = None,
          countryOfResidence = Some("GB")
        )
      }

      "Country of residence is set to outside the UK in 5mld mode" in {
        val userAnswers =
          emptyUserAnswers
            .set(NamePage(index0), "Company Name").success.value
            .set(IncomeYesNoPage(index0), false).success.value
            .set(IncomePage(index0), 100).success.value
            .set(CountryOfResidenceYesNoPage(index0), true).success.value
            .set(CountryOfResidenceInTheUkYesNoPage(index0), false).success.value
            .set(CountryOfResidencePage(index0), "FR").success.value
            .set(AddressYesNoPage(index0), false).success.value

        val company = mapper.build(userAnswers)

        company mustBe defined
        company.value.head mustBe CompanyType(
          organisationName = "Company Name",
          beneficiaryDiscretion = Some(false),
          beneficiaryShareOfIncome = Some("100"),
          identification = None,
          countryOfResidence = Some("FR")
        )
      }

      "must not be able to create IndividualDetailsType when incomplete data " in {
        val userAnswers =
          emptyUserAnswers
            .set(NamePage(index0), "Company Name").success.value

        mapper.build(userAnswers) mustNot be(defined)
      }
    }
  }
}

