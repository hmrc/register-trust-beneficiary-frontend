/*
 * Copyright 2026 HM Revenue & Customs
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
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.register.beneficiaries.classofbeneficiaries._

class ClassOfBeneficiariesNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks {

  val navigator = new ClassOfBeneficiariesNavigator

  val index = 0

  "Class of beneficiaries navigator" must {

    "Description page -> Add-to page" in
      navigator
        .nextPage(ClassBeneficiaryDescriptionPage(index), fakeDraftId, emptyUserAnswers)
        .mustBe(controllers.register.beneficiaries.routes.AddABeneficiaryController.onPageLoad(draftId))
  }

}
