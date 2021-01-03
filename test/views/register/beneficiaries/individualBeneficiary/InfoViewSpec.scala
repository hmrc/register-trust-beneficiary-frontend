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

package views.register.beneficiaries.individualBeneficiary

import controllers.register.beneficiaries.routes
import views.behaviours.ViewBehaviours
import views.html.register.beneficiaries.individualBeneficiary.InfoView

class InfoViewSpec extends ViewBehaviours {

  "IndividualBeneficiaryInfo view" must {

    val view = viewFor[InfoView](Some(emptyUserAnswers))

    val applyView = view.apply(fakeDraftId)(fakeRequest, messages)

    behave like normalPage(applyView, "individualBeneficiaryInfo",
      "caption",
      "subheading1",
      "paragraph1",
      "bulletpoint1",
      "bulletpoint2",
      "bulletpoint3",
      "bulletpoint4",
      "bulletpoint5",
      "paragraph2",
      "paragraph3",
      "subheading2",
      "paragraph4",
      "subheading3",
      "paragraph5",
      "subheading4",
      "paragraph6",
      "bulletpoint6",
      "bulletpoint7",
      "bulletpoint8",
      "paragraph7",
      "subheading5",
      "paragraph8"
    )

    behave like pageWithBackLink(applyView)

    behave like pageWithContinueButton(applyView, routes.WhatTypeOfBeneficiaryController.onPageLoad(fakeDraftId).url )
  }
}
