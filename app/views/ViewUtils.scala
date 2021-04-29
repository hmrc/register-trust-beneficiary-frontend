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

package views

import play.api.data.{Form, FormError}
import play.api.i18n.Messages

object ViewUtils {

  def errorPrefix(form: Form[_])(implicit messages: Messages): String = {
    if (form.hasErrors || form.hasGlobalErrors) s"${messages("error.browser.title.prefix")} " else ""
  }

  def errorHref(error: FormError): String = {

    val suffix = error.args match {
      case x if x.contains("day") || x.contains("month") || x.contains("year") =>
        s"_${error.args.head}"
      case _ =>
        val isSingleDateField = error.message.toLowerCase.contains("date") && !error.message.toLowerCase.contains("yesno")
        if (error.key.toLowerCase.contains("date") || isSingleDateField) {
          "_day"
        } else {
          ""
        }
    }
    s"${error.key}$suffix"
  }

  def breadcrumbTitle(title: String)(implicit messages: Messages): String = {
    s"$title - ${messages("entity.beneficiary")} - ${messages("site.service_name")} - GOV.UK"
  }
}
