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

package navigation

import base.SpecBase
import controllers.register.beneficiaries.companyoremploymentrelated.company.{routes => CompanyRoutes}
import generators.Generators
import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.register.beneficiaries.company._

class CompanyBeneficiaryNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new CompanyBeneficiaryNavigator
  val index = 0

  "Company beneficiary navigator" must {

    "go to DiscretionYesNo from NamePage" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(NamePage(index), fakeDraftId, userAnswers)
            .mustBe(CompanyRoutes.DiscretionYesNoController.onPageLoad(index, fakeDraftId))
      }
    }

    "go to AddressYesNo from ShareOfIncomePage" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(ShareOfIncomePage(index), fakeDraftId, userAnswers)
            .mustBe(CompanyRoutes.AddressYesNoController.onPageLoad(index, fakeDraftId))
      }
    }

  }
}
