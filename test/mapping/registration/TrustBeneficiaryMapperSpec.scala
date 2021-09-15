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
import models.{AddressType, BeneficiaryTrustType, IdentificationOrgType}
import org.scalatest.{MustMatchers, OptionValues}
import pages.register.beneficiaries.charityortrust.trust._
import utils.Constants._

class TrustBeneficiaryMapperSpec extends SpecBase with MustMatchers
  with OptionValues with Generators {

  private val trustBeneficiariesMapper: TrustBeneficiaryMapper = injector.instanceOf[TrustBeneficiaryMapper]
  private val index = 0
  private val index1 = 1
  private val trustName = "Trust Name"

  "TrustBeneficiariesMapper" when {

    "when user answers is empty" must {

      "must not be able to create BeneficiaryTrustType" in {

        val userAnswers = emptyUserAnswers

        trustBeneficiariesMapper.build(userAnswers) mustNot be(defined)
      }
    }

    "when user answers is not empty" must {

      "must be able to create BeneficiaryTrustType" when {

        "Share In Income is set" in {
          val userAnswers =
            emptyUserAnswers
              .set(NamePage(index), trustName).success.value
              .set(DiscretionYesNoPage(index), false).success.value
              .set(ShareOfIncomePage(index), 100).success.value
              .set(AddressYesNoPage(index), false).success.value

          val trusts = trustBeneficiariesMapper.build(userAnswers)

          trusts mustBe defined
          trusts.value.head mustBe BeneficiaryTrustType(
            organisationName = trustName,
            beneficiaryDiscretion = Some(false),
            beneficiaryShareOfIncome = Some("100"),
            identification = None,
            countryOfResidence = None
          )
        }

        "Country of residence is set to the UK in 5mld mode" in {
          val userAnswers =
            emptyUserAnswers
              .set(NamePage(index), trustName).success.value
              .set(DiscretionYesNoPage(index), false).success.value
              .set(ShareOfIncomePage(index), 100).success.value
              .set(CountryOfResidenceYesNoPage(index), true).success.value
              .set(CountryOfResidenceInTheUkYesNoPage(index), true).success.value
              .set(AddressYesNoPage(index), false).success.value

          val trusts = trustBeneficiariesMapper.build(userAnswers)

          trusts mustBe defined
          trusts.value.head mustBe BeneficiaryTrustType(
            organisationName = trustName,
            beneficiaryDiscretion = Some(false),
            beneficiaryShareOfIncome = Some("100"),
            identification = None,
            countryOfResidence = Some(GB)
          )
        }

        "Country of residence is set to outside the UK in 5mld mode" in {
          val userAnswers =
            emptyUserAnswers
              .set(NamePage(index), trustName).success.value
              .set(DiscretionYesNoPage(index), false).success.value
              .set(ShareOfIncomePage(index), 100).success.value
              .set(CountryOfResidenceYesNoPage(index), true).success.value
              .set(CountryOfResidenceInTheUkYesNoPage(index), false).success.value
              .set(CountryOfResidencePage(index), "FR").success.value
              .set(AddressYesNoPage(index), false).success.value

          val trusts = trustBeneficiariesMapper.build(userAnswers)

          trusts mustBe defined
          trusts.value.head mustBe BeneficiaryTrustType(
            organisationName = trustName,
            beneficiaryDiscretion = Some(false),
            beneficiaryShareOfIncome = Some("100"),
            identification = None,
            countryOfResidence = Some("FR")
          )
        }

        "UK Address is set" in {
          val userAnswers =
            emptyUserAnswers
              .set(NamePage(index), trustName).success.value
              .set(DiscretionYesNoPage(index), true).success.value
              .set(AddressYesNoPage(index), true).success.value
              .set(AddressUKYesNoPage(index), true).success.value
              .set(AddressUKPage(index),
                UKAddress("Line1", "Line2", None, Some("Newcastle"), "NE62RT")).success.value

          val trusts = trustBeneficiariesMapper.build(userAnswers)

          trusts mustBe defined
          trusts.value.head mustBe BeneficiaryTrustType(
            organisationName = trustName,
            beneficiaryDiscretion = Some(true),
            beneficiaryShareOfIncome = None,
            identification = Some(IdentificationOrgType(
              None,
              address = Some(
                AddressType("Line1", "Line2", None, Some("Newcastle"), Some("NE62RT"), GB)
              )
            )
          ),
            countryOfResidence = None)
        }

        "International Address is set" in {
          val userAnswers =
            emptyUserAnswers
              .set(NamePage(index), trustName).success.value
              .set(DiscretionYesNoPage(index), true).success.value
              .set(AddressYesNoPage(index), true).success.value
              .set(AddressUKYesNoPage(index), false).success.value
              .set(AddressInternationalPage(index),
                InternationalAddress("Line1", "Line2", Some("Paris"), "FR")).success.value

          val trusts = trustBeneficiariesMapper.build(userAnswers)

          trusts mustBe defined
          trusts.value.head mustBe BeneficiaryTrustType(
            organisationName = trustName,
            beneficiaryDiscretion = Some(true),
            beneficiaryShareOfIncome = None,
            identification = Some(IdentificationOrgType(
              None,
              address = Some(
                AddressType("Line1", "Line2", Some("Paris"), None, None, "FR")
              )
            )
          ),
            countryOfResidence = None)
        }

      }

      "must be able to create multiple BeneficiaryTrustType" in {
        val userAnswers =
          emptyUserAnswers
            .set(NamePage(index), trustName).success.value
            .set(DiscretionYesNoPage(index), true).success.value
            .set(AddressYesNoPage(index), true).success.value
            .set(AddressUKYesNoPage(index), true).success.value
            .set(AddressUKPage(index),
              UKAddress("Line1", "Line2", None, Some("Newcastle"), "NE62RT")).success.value

            .set(NamePage(index1), trustName).success.value
            .set(DiscretionYesNoPage(index1), true).success.value
            .set(AddressYesNoPage(index1), true).success.value
            .set(AddressUKYesNoPage(index1), false).success.value
            .set(AddressInternationalPage(index1),
            InternationalAddress("Line1", "Line2", Some("Paris"), "FR")).success.value


        val individuals = trustBeneficiariesMapper.build(userAnswers)

        individuals mustBe defined
        individuals.value mustBe
          List(
            BeneficiaryTrustType(
              organisationName = trustName,
              beneficiaryDiscretion = Some(true),
              beneficiaryShareOfIncome = None,
              identification = Some(IdentificationOrgType(
                None,
                address = Some(
                  AddressType("Line1", "Line2", None, Some("Newcastle"), Some("NE62RT"), GB)
                )
              )
            ),
              countryOfResidence = None),
            BeneficiaryTrustType(
              organisationName = trustName,
              beneficiaryDiscretion = Some(true),
              beneficiaryShareOfIncome = None,
              identification = Some(IdentificationOrgType(
                None,
                address = Some(
                  AddressType("Line1", "Line2", Some("Paris"), None, None, "FR")
                )
              )
            ),
              countryOfResidence = None)
          )
      }

      "must be able to create BeneficiaryTrustType with minimum NonTaxable data" in {
        val userAnswers =
          emptyUserAnswers
            .set(NamePage(index), trustName).success.value

        val trusts = trustBeneficiariesMapper.build(userAnswers)

        trusts.value.head mustBe BeneficiaryTrustType(
          organisationName = trustName,
          beneficiaryDiscretion = None,
          beneficiaryShareOfIncome = None,
          identification = None,
          countryOfResidence = None)
      }

      "must be able to create BeneficiaryTrustType with full NonTaxable data" in {
        val userAnswers =
          emptyUserAnswers
            .set(NamePage(index), trustName).success.value
            .set(CountryOfResidencePage(index), GB).success.value

        val trusts = trustBeneficiariesMapper.build(userAnswers)

        trusts.value.head mustBe BeneficiaryTrustType(
          organisationName = trustName,
          beneficiaryDiscretion = None,
          beneficiaryShareOfIncome = None,
          identification = None,
          countryOfResidence = Some(GB))
      }

      "must not be able to create BeneficiaryTrustType when incomplete data " in {
        val userAnswers =
          emptyUserAnswers
            .set(DiscretionYesNoPage(index), true).success.value

        trustBeneficiariesMapper.build(userAnswers) mustNot be(defined)
      }
    }
  }
}
