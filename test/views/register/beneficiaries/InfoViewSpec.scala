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

package views.register.beneficiaries

import views.behaviours.ViewBehaviours
import views.html.register.beneficiaries.InfoView

class InfoViewSpec extends ViewBehaviours {

  "Info view" when {

    "a non-taxable trust" must {

      val view = viewFor[InfoView](Some(emptyUserAnswers))
      val applyView = view.apply(fakeDraftId, isTaxable = false)(fakeRequest, messages)

      behave like normalPageTitleWithSectionSubheading(applyView, "beneficiaryInfo.5mld",
        "subheading1",
        "paragraph11",
        "bulletpoint11",
        "bulletpoint12",
        "bulletpoint13",
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
        "paragraph65",
        "details",
        "details.subheading1",
        "details.paragraph1",
        "details.subheading2",
        "details.paragraph2",
        "details.subheading3",
        "details.paragraph3",
        "subheading7",
        "paragraph71.nonTaxable",
        "paragraph72.nonTaxable"
      )

      behave like pageWithBackLink(applyView)
      behave like pageWithContinueButton(applyView)

    }

    "a taxable trust" must {

      val view = viewFor[InfoView](Some(emptyUserAnswers))
      val applyView = view.apply(fakeDraftId, isTaxable = true)(fakeRequest, messages)

      behave like normalPageTitleWithSectionSubheading(applyView, "beneficiaryInfo.5mld",
        "caption",
        "subheading1",
        "paragraph11",
        "bulletpoint11",
        "bulletpoint12",
        "bulletpoint13",
        "paragraph12",
        "bulletpoint14",
        "bulletpoint15",
        "bulletpoint16",
        "bulletpoint17",
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
        "paragraph71.taxable",
        "paragraph72.taxable"
      )

      behave like pageWithBackLink(applyView)
      behave like pageWithContinueButton(applyView)

    }

  }
}
