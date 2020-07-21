#!/bin/bash

echo ""
echo "Applying migration CharityName"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /charityName                        controllers.CharityNameController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /charityName                        controllers.CharityNameController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeCharityName                  controllers.CharityNameController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeCharityName                  controllers.CharityNameController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "charityName.title = charityName" >> ../conf/messages.en
echo "charityName.heading = charityName" >> ../conf/messages.en
echo "charityName.CharityName = CharityName" >> ../conf/messages.en
echo "charityName.field2 = field2" >> ../conf/messages.en
echo "charityName.checkYourAnswersLabel = charityName" >> ../conf/messages.en
echo "charityName.error.CharityName.required = Enter CharityName" >> ../conf/messages.en
echo "charityName.error.field2.required = Enter field2" >> ../conf/messages.en
echo "charityName.error.CharityName.length = CharityName must be 50 characters or less" >> ../conf/messages.en
echo "charityName.error.field2.length = field2 must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryCharityNameUserAnswersEntry: Arbitrary[(CharityNamePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[CharityNamePage.type]";\
    print "        value <- arbitrary[CharityName].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryCharityNamePage: Arbitrary[CharityNamePage.type] =";\
    print "    Arbitrary(CharityNamePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryCharityName: Arbitrary[CharityName] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        CharityName <- arbitrary[String]";\
    print "        field2 <- arbitrary[String]";\
    print "      } yield CharityName(CharityName, field2)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(CharityNamePage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def charityName: Option[AnswerRow] = userAnswers.get(CharityNamePage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        HtmlFormat.escape(messages(\"charityName.checkYourAnswersLabel\")),";\
     print "        HtmlFormat.escape(s\"${x.CharityName} ${x.field2}\"),";\
     print "        routes.CharityNameController.onPageLoad(CheckMode).url";\
     print "      )"
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration CharityName completed"
