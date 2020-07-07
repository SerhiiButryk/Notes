#ifndef ANDROID_SAMPLES_JSON_PARSER_H
#define ANDROID_SAMPLES_JSON_PARSER_H

#include <memory>
#include <string>
#include <vector>
#include <exception>

/**
 *  Represent json object structure
 *
 * */

namespace MYLIB
{
    struct JsonObject
    {
        std::string key;
        std::string value;
    };

    class Json
    {
        private:
            std::string _json;
            std::vector<JsonObject> _objects;
            
        public:
            Json() = default;
            explicit Json(const std::string& json);

            void parse();
            void parse(const std::string& json);

            const std::vector<MYLIB::JsonObject>& getElements();

        private:
            void parseJson();
            JsonObject parseObject(std::string::iterator& beg, std::string::const_iterator& end);
            void skipSpaces(std::string::iterator& beg, std::string::const_iterator& end);
            std::vector<JsonObject> parseJson(std::string::iterator& beg, std::string::const_iterator& end);
            JsonObject processObject(std::string::iterator& beg, std::string::const_iterator& end);
            std::string readString(std::string::iterator& beg, std::string::const_iterator& end);

        class ParseException : public std::exception
            {
                std::string _what;
            public:
                ParseException(std::string message) : std::exception(), _what(message) {}

                const char* what() const throw() override { return _what.c_str(); }
            };
    };
}

#endif
