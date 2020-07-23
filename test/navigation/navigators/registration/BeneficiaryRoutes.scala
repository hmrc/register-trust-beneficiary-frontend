/*
 * Copyright 2020 HM Revenue & Customs
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

package navigation.navigators.registration

import base.SpecBase
import controllers.register.beneficiaries.routes
import generators.Generators
import models.registration.pages.{CharityOrTrust, WhatTypeOfBeneficiary}
import models.{NormalMode, UserAnswers}
import navigation.Navigator
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.register.beneficiaries._
import pages.register.beneficiaries.classofbeneficiaries.ClassBeneficiaryDescriptionPage
import pages.register.beneficiaries.individual._

trait BeneficiaryRoutes {
  self: ScalaCheckPropertyChecks with Generators with SpecBase =>

  def beneficiaryRoutes()(implicit navigator: Navigator): Unit = {

    "go to CharityOrTrust from WhatTypeOfBeneficiaryPage when CharityOrTrust option selected " in {

      val answers = emptyUserAnswers.set(WhatTypeOfBeneficiaryPage, value = WhatTypeOfBeneficiary.CharityOrTrust).success.value
      navigator.nextPage(WhatTypeOfBeneficiaryPage, NormalMode, fakeDraftId, answers)
        .mustBe(controllers.register.beneficiaries.charityortrust.routes.CharityOrTrustController.onPageLoad(NormalMode, fakeDraftId))
    }

    "go to CharityName for index 0 from CharityOrTrust when Charity option selected " in {
      val answers = emptyUserAnswers.set(pages.register.beneficiaries.charityortrust.CharityOrTrustPage, value = CharityOrTrust.Charity).success.value
      navigator.nextPage(pages.register.beneficiaries.charityortrust.CharityOrTrustPage, NormalMode, fakeDraftId, answers)
        .mustBe(controllers.register.beneficiaries.charityortrust.charity.routes.CharityNameController.onPageLoad(0, fakeDraftId))
    }

    "go to AddABeneficiaryPage from AnswersPage" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(AnswersPage, NormalMode, fakeDraftId, userAnswers)
            .mustBe(routes.AddABeneficiaryController.onPageLoad(fakeDraftId))
      }
    }
    
    "go to AddABeneficiaryPage from ClassBeneficiaryDescriptionPage" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(ClassBeneficiaryDescriptionPage(0), NormalMode, fakeDraftId, userAnswers)
            .mustBe(routes.AddABeneficiaryController.onPageLoad(fakeDraftId))
      }
    }
  }

}
