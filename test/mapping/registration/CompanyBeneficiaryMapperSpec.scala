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
import models.core.pages.{InternationalAddress, UKAddress}
import models.{AddressType, CompanyType, IdentificationOrgType}
import pages.register.beneficiaries.companyoremploymentrelated.company._
import pages.register.beneficiaries.companyoremploymentrelated.company.mld5._
import utils.Constants._

class CompanyBeneficiaryMapperSpec extends SpecBase with Generators {

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
              .set(NamePage(index0), "Company Name").right.get
              .set(IncomeYesNoPage(index0), true).right.get
              .set(AddressYesNoPage(index0), false).right.get

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
              .set(NamePage(index0), "Company Name").right.get
              .set(IncomeYesNoPage(index0), false).right.get
              .set(IncomePage(index0), 42).right.get
              .set(AddressYesNoPage(index0), false).right.get

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

        "income value is not set" in {
          val userAnswers =
            emptyUserAnswers
              .set(NamePage(index0), "Company Name").right.get
              .set(CountryOfResidenceYesNoPage(index0), true).right.get
              .set(CountryOfResidenceInTheUkYesNoPage(index0), true).right.get

          val companies = mapper.build(userAnswers)

          companies mustBe defined
          companies.value.head mustBe CompanyType(
            organisationName = "Company Name",
            beneficiaryDiscretion = None,
            beneficiaryShareOfIncome = None,
            identification = None,
            countryOfResidence = Some("GB")
          )
        }

        "UK Address is set" in {
          val userAnswers =
            emptyUserAnswers
              .set(NamePage(index0), "Company Name").right.get
              .set(IncomeYesNoPage(index0), true).right.get
              .set(AddressYesNoPage(index0), true).right.get
              .set(AddressUKYesNoPage(index0), true).right.get
              .set(AddressUKPage(index0),
                UKAddress("Line1", "Line2", Some("Line3"), Some("Newcastle"), "NE62RT")).right.get

          val companies = mapper.build(userAnswers)

          companies mustBe defined
          companies.value.head mustBe CompanyType(
            organisationName = "Company Name",
            beneficiaryDiscretion = Some(true),
            beneficiaryShareOfIncome = None,
            identification = Some(IdentificationOrgType(
              None,
              address = Some(
                AddressType("Line1", "Line2", Some("Line3"), Some("Newcastle"), Some("NE62RT"), GB)
              ))),
            countryOfResidence = None
          )
        }
        "International Address is set" in {
          val userAnswers =
            emptyUserAnswers
              .set(NamePage(index0), "Company Name").right.get
              .set(IncomeYesNoPage(index0), true).right.get
              .set(AddressYesNoPage(index0), true).right.get
              .set(AddressUKYesNoPage(index0), false).right.get
              .set(AddressInternationalPage(index0),
                InternationalAddress("Line1", "Line2", Some("Line3"), "US")).right.get

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
            .set(NamePage(index0), "Company 1").right.get
            .set(IncomeYesNoPage(index0), false).right.get
            .set(IncomePage(index0), 100).right.get

            .set(NamePage(index1), "Company 2").right.get
            .set(IncomeYesNoPage(index1), true).right.get
            .set(AddressYesNoPage(index1), true).right.get
            .set(AddressUKYesNoPage(index1), true).right.get
            .set(AddressUKPage(index1),
              UKAddress("line1", "line2", None, None, "NE62RT")).right.get


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
                    AddressType("line1", "line2", None, None, Some("NE62RT"), GB)
                  ))),
              countryOfResidence = None
            )
          )
      }


      "Country of residence is set to the UK" in {
        val userAnswers =
          emptyUserAnswers
            .set(NamePage(index0), "Company Name").right.get
            .set(IncomeYesNoPage(index0), false).right.get
            .set(IncomePage(index0),60).right.get
            .set(CountryOfResidenceYesNoPage(index0), true).right.get
            .set(CountryOfResidenceInTheUkYesNoPage(index0), true).right.get
            .set(AddressYesNoPage(index0), false).right.get

        val company = mapper.build(userAnswers)

        company mustBe defined
        company.value.head mustBe CompanyType(
          organisationName = "Company Name",
          beneficiaryDiscretion = Some(false),
          beneficiaryShareOfIncome = Some("60"),
          identification = None,
          countryOfResidence = Some(GB)
        )
      }

      "Country of residence is set to outside the UK" in {
        val userAnswers =
          emptyUserAnswers
            .set(NamePage(index0), "Company Name").right.get
            .set(IncomeYesNoPage(index0), false).right.get
            .set(IncomePage(index0), 100).right.get
            .set(CountryOfResidenceYesNoPage(index0), true).right.get
            .set(CountryOfResidenceInTheUkYesNoPage(index0), false).right.get
            .set(CountryOfResidencePage(index0), "FR").right.get
            .set(AddressYesNoPage(index0), false).right.get

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

      "must be able to create IndividualDetailsType with minimum data" in {
        val userAnswers =
          emptyUserAnswers
            .set(NamePage(index0), "Company Name").right.get

        mapper.build(userAnswers) must be(defined)
      }
    }
  }
}

