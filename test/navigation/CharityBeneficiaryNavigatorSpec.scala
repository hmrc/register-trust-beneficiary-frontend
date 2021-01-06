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

package navigation

import base.SpecBase
import controllers.register.beneficiaries.charityortrust.charity.{routes => rts}
import controllers.register.beneficiaries.charityortrust.charity.nonTaxable.{routes => ntRts}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.register.beneficiaries.charityortrust.charity._
import pages.register.beneficiaries.charityortrust.charity.nonTaxable.{CountryOfResidenceInTheUkYesNoPage, CountryOfResidencePage, CountryOfResidenceYesNoPage}
import services.FeatureFlagService

import scala.concurrent.Future

class CharityBeneficiaryNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks {

  val mockFeatureFlgService: FeatureFlagService = mock[FeatureFlagService]

  val navigator = new CharityBeneficiaryNavigator()

  val index = 0

  "Charity beneficiary navigator" must {

    "a 4mld trust" must {

      "Name page -> Discretion yes no page" in {
        navigator.nextPage(CharityNamePage(index), fakeDraftId, emptyUserAnswers)
          .mustBe(rts.AmountDiscretionYesNoController.onPageLoad(index, fakeDraftId))
      }

      "Discretion yes no page -> Yes -> Address yes no page" in {
        val answers = emptyUserAnswers
          .set(AmountDiscretionYesNoPage(index), true).success.value

        navigator.nextPage(AmountDiscretionYesNoPage(index), fakeDraftId, answers)
          .mustBe(rts.AddressYesNoController.onPageLoad(index, fakeDraftId))
      }

      "Discretion yes no page -> No -> How much income page" in {
        val answers = emptyUserAnswers
          .set(AmountDiscretionYesNoPage(index), false).success.value

        navigator.nextPage(AmountDiscretionYesNoPage(index), fakeDraftId, answers)
          .mustBe(rts.HowMuchIncomeController.onPageLoad(index, fakeDraftId))
      }

      "How much income page -> Do you know address page" in {
        navigator.nextPage(HowMuchIncomePage(index), fakeDraftId, emptyUserAnswers)
          .mustBe(rts.AddressYesNoController.onPageLoad(index, fakeDraftId))
      }


      "Do you know address page -> Yes -> Is address in UK page" in {
        val answers = emptyUserAnswers
          .set(AddressYesNoPage(index), true).success.value

        navigator.nextPage(AddressYesNoPage(index), fakeDraftId, answers)
          .mustBe(rts.AddressInTheUkYesNoController.onPageLoad(index, fakeDraftId))
      }

      "Do you know address page -> No -> Check answers page" in {
        val answers = emptyUserAnswers
          .set(AddressYesNoPage(index), false).success.value

        navigator.nextPage(AddressYesNoPage(index), fakeDraftId, answers)
          .mustBe(rts.CharityAnswersController.onPageLoad(index, fakeDraftId))
      }


      "Is address in UK page -> Yes -> UK address page" in {
        val answers = emptyUserAnswers
          .set(AddressInTheUkYesNoPage(index), true).success.value

        navigator.nextPage(AddressInTheUkYesNoPage(index), fakeDraftId, answers)
          .mustBe(rts.CharityAddressUKController.onPageLoad(index, fakeDraftId))
      }

      "Is address in UK page -> No -> International address page" in {
        val answers = emptyUserAnswers
          .set(AddressInTheUkYesNoPage(index), false).success.value

        navigator.nextPage(AddressInTheUkYesNoPage(index), fakeDraftId, answers)
          .mustBe(rts.CharityInternationalAddressController.onPageLoad(index, fakeDraftId))
      }

      "UK address page -> Check answers page" in {
        navigator.nextPage(CharityAddressUKPage(index), fakeDraftId, emptyUserAnswers)
          .mustBe(rts.CharityAnswersController.onPageLoad(index, fakeDraftId))
      }

      "International address page -> Check answers page" in {
        navigator.nextPage(CharityInternationalAddressPage(index), fakeDraftId, emptyUserAnswers)
          .mustBe(rts.CharityAnswersController.onPageLoad(index, fakeDraftId))
      }
    }

    "a 5mld trust" must {

      "Discretion yes no page -> Yes -> Address yes no page" in {
        when(mockFeatureFlgService.is5mldEnabled()(any(), any())).thenReturn(Future.successful(true))

        val answers = emptyUserAnswers
          .set(AmountDiscretionYesNoPage(index), true).success.value

        navigator.nextPage(AmountDiscretionYesNoPage(index), fakeDraftId, fiveMldEnabled = true, answers)
          .mustBe(ntRts.CountryOfResidenceYesNoController.onPageLoad(index, draftId))
      }

      "CountryOfResidence yes no page -> No -> Address yes no page" in {
        val answers = emptyUserAnswers
          .set(CountryOfResidenceYesNoPage(index), false).success.value

        navigator.nextPage(CountryOfResidenceYesNoPage(index), draftId, fiveMldEnabled = true, answers)
          .mustBe(rts.AddressYesNoController.onPageLoad(index, draftId))
      }

      "CountryOfResidence yes no page -> Yes -> CountryOfResidence Uk yes no page" in {
        val answers = emptyUserAnswers
          .set(CountryOfResidenceYesNoPage(index), true).success.value

        navigator.nextPage(CountryOfResidenceYesNoPage(index), draftId, fiveMldEnabled = true, answers)
          .mustBe(ntRts.CountryOfResidenceInTheUkYesNoController.onPageLoad(index, draftId))
      }


      "CountryOfResidence Uk yes no page -> Yes -> Address yes no page" in {
        val answers = emptyUserAnswers
          .set(CountryOfResidenceInTheUkYesNoPage(index), true).success.value

        navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), draftId, fiveMldEnabled = true, answers)
          .mustBe(rts.AddressYesNoController.onPageLoad(index, draftId))
      }

      "CountryOfResidence Uk yes no page -> No -> Address yes no page" in {
        val answers = emptyUserAnswers
          .set(CountryOfResidenceInTheUkYesNoPage(index), false).success.value

        navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), draftId, fiveMldEnabled = true, answers)
          .mustBe(ntRts.CountryOfResidenceController.onPageLoad(index, draftId))
      }

      "CountryOfResidence page -> Address yes no page" in {
        navigator.nextPage(CountryOfResidencePage(index), draftId, emptyUserAnswers)
          .mustBe(rts.AddressYesNoController.onPageLoad(index, draftId))
      }

      "How much income page -> CountryOfResidence Yes No page" in {

        navigator.nextPage(HowMuchIncomePage(index), draftId, fiveMldEnabled = true, emptyUserAnswers)
          .mustBe(ntRts.CountryOfResidenceYesNoController.onPageLoad(index, draftId))
      }
    }

  }

}
