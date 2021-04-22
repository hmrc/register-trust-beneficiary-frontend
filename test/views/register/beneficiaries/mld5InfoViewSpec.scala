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
import views.html.register.beneficiaries.mld5InfoView

class mld5InfoViewSpec extends ViewBehaviours {

  "mld5Info view" when {

    "a non-taxable trust" must {

      val view = viewFor[mld5InfoView](Some(emptyUserAnswers))
      val applyView = view.apply(fakeDraftId, false)(fakeRequest, messages)

      behave like normalPageTitleWithCaption(applyView, "beneficiaryInfo.5mld",
        "caption",
        "subheading1",
        "paragraph1",
        "bulletpoint11",
        "bulletpoint12",
        "bulletpoint13",
        "subheading2",
        "paragraph2",
        "bulletpoint21",
        "bulletpoint22",
        "subheading3",
        "paragraph31",
        "paragraph32",
        "bulletpoint31",
        "bulletpoint32",
        "bulletpoint33",
        "bulletpoint34",
        "paragraph33",
        "subheading4",
        "paragraph4",
        "subheading5",
        "paragraph5",
        "subheading6",
        "paragraph61",
        "bulletpoint61",
        "bulletpoint62",
        "paragraph62",
        "paragraph63",
        "paragraph64",
        "bulletpoint63",
        "bulletpoint64",
        "paragraph65",
        "details",
        "details.subheading1",
        "details.paragraph1",
        "details.subheading2",
        "details.paragraph2",
        "details.subheading3",
        "details.paragraph3",
        "subheading7",
        "paragraph71",
        "paragraph72"
      )

      behave like pageWithBackLink(applyView)
      behave like pageWithContinueButton(applyView, routes.WhatTypeOfBeneficiaryController.onPageLoad(fakeDraftId).url )

    }

    "a taxable trust" must {

      val view = viewFor[mld5InfoView](Some(emptyUserAnswers))
      val applyView = view.apply(fakeDraftId, true)(fakeRequest, messages)

      behave like normalPageTitleWithCaption(applyView, "beneficiaryInfo.5mld",
        "caption",
        "subheading1",
        "paragraph1",
        "bulletpoint11",
        "bulletpoint12",
        "bulletpoint13",
        "subheading2",
        "paragraph2",
        "bulletpoint21",
        "bulletpoint22",
        "subheading3",
        "paragraph31",
        "paragraph32",
        "bulletpoint31",
        "bulletpoint32",
        "bulletpoint33",
        "bulletpoint34",
        "paragraph33",
        "subheading4",
        "paragraph4",
        "subheading5",
        "paragraph5",
        "subheading6",
        "paragraph61",
        "bulletpoint61",
        "bulletpoint62",
        "paragraph62",
        "paragraph63",
        "paragraph64",
        "bulletpoint63",
        "bulletpoint64",
        "paragraph65",
        "details",
        "details.subheading1",
        "details.paragraph1",
        "details.subheading2",
        "details.paragraph2",
        "details.subheading3",
        "details.paragraph3",
        "subheading7",
        "paragraph71",
        "paragraph72"
      )

      behave like pageWithBackLink(applyView)
      behave like pageWithContinueButton(applyView, routes.WhatTypeOfBeneficiaryController.onPageLoad(fakeDraftId).url )

    }

  }
}
