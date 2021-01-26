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

package views.register.beneficiaries

import controllers.register.beneficiaries.routes
import views.behaviours.ViewBehaviours
import views.html.register.beneficiaries.NonTaxableInfoView

class NonTaxableInfoViewSpec extends ViewBehaviours {

  "NonTaxableInfo view" must {

    val view = viewFor[NonTaxableInfoView](Some(emptyUserAnswers))

    val applyView = view.apply(fakeDraftId)(fakeRequest, messages)

    behave like normalPageTitleWithCaption(applyView, "beneficiaryInfo.5mld",
      "caption",
      "subheading1",
      "paragraph1",
      "bulletpoint1",
      "bulletpoint2",
      "bulletpoint3",
      "paragraph2",
      "paragraph3",
      "subheading2",
      "paragraph4",
      "subheading3",
      "paragraph5",
      "subheading4",
      "paragraph6",
      "bulletpoint4",
      "bulletpoint5",
      "bulletpoint6",
      "paragraph7",
      "paragraph8",
      "details",
      "details.subheading1",
      "details.paragraph1",
      "details.subheading2",
      "details.paragraph2",
      "details.subheading3",
      "details.paragraph3",
      "subheading5",
      "paragraph9"
    )

    behave like pageWithBackLink(applyView)

    behave like pageWithContinueButton(applyView, routes.WhatTypeOfBeneficiaryController.onPageLoad(fakeDraftId).url )
  }
}
