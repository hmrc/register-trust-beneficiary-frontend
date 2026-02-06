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

package views.register.beneficiaries.individualBeneficiary.mld5

import forms.YesNoFormProvider
import models.core.pages.FullName
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.register.beneficiaries.individualBeneficiary.mld5.CountryOfResidenceYesNoView

class CountryOfResidenceYesNoViewSpec extends YesNoViewBehaviours {

  val prefix       = "individualBeneficiary.5mld.countryOfResidenceYesNo"
  val index        = 0
  val name: String = FullName("FirstName", None, "LastName").toString

  val form: Form[Boolean] = new YesNoFormProvider().withPrefix(prefix)

  "countryOfResidenceYesNo view" must {

    val view = viewFor[CountryOfResidenceYesNoView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, fakeDraftId, index, name)(fakeRequest, messages)

    behave like dynamicTitlePage(applyView(form), prefix, name)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, prefix, Some(prefix), Seq(name))

    behave like pageWithASubmitButton(applyView(form))
  }

}
