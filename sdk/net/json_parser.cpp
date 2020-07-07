#include "json_parser.h"
#include "../common/log.h"

const static std::string TAG = "JSON"; 

MYLIB::Json::Json(const std::string &json) : _json(json) {
}

void MYLIB::Json::parse(const std::string &json) {
    _json = json;
    parseJson();
}

void MYLIB::Json::parse() {
    parseJson();
}

void MYLIB::Json::parseJson() {
    std::string::iterator beg = _json.begin();
    std::string::const_iterator end = _json.end();

    if (beg == end)
        return;

    try {
        _objects = parseJson(beg, end);
    } catch (const ParseException& e) {
        LOG_ERROR(TAG, e.what());
    }
}

std::vector<MYLIB::JsonObject> MYLIB::Json::parseJson(std::string::iterator& beg, 
    std::string::const_iterator& end)
{
    std::vector<JsonObject> objects;

    while (beg != end) 
    {
        skipSpaces(beg, end);

        if (*beg == '{')
        {
            JsonObject object = parseObject(beg, end);
            objects.push_back(object);
            skipSpaces(beg, end);
        }


        if (*beg == ',')
        {
            JsonObject object = parseObject(beg, end);
            objects.push_back(object);
            skipSpaces(beg, end);
        } else if (*beg == '}') {
            // all objects are processed
            break;
        } else {
            throw ParseException("Invalid json object");
        }

    }

    return objects;
}

MYLIB::JsonObject MYLIB::Json::parseObject(std::string::iterator& beg, std::string::const_iterator& end)
{
    // Move to the next element
    ++beg;
    skipSpaces(beg, end);
    return processObject(beg, end);
}

void MYLIB::Json::skipSpaces(std::string::iterator& beg, std::string::const_iterator& end)
{
    if (*beg != ' ')
        return;

    while (beg != end)
    {
        if (*beg != ' ' && *beg != '\n' && *beg != '\t')
            break;

        ++beg;
    }
}

MYLIB::JsonObject MYLIB::Json::processObject(std::string::iterator& beg, std::string::const_iterator& end)
{
    JsonObject object;

    while (beg != end)
    {
        if (*beg == '"') {
            ++beg;
            object.key = readString(beg, end);
        }

        skipSpaces(beg, end);

        if (*beg != ':')
        {
            throw ParseException("No object value found");
        } else {
            // skip ':'
            ++beg;
            skipSpaces(beg, end);

            if (*beg == '"')
            {
                ++beg;    
                object.value = readString(beg, end);
                break;
            }
        }

        ++beg;
    }

    return object;
}

std::string MYLIB::Json::readString(std::string::iterator& beg, std::string::const_iterator& end)
{
    std::string name;

    while (*beg != '"')
    {
        if (beg == end)
            break;

        name += *beg;
        ++beg;
    }

    // skip close '"'
    ++beg;

    return name;
}

const std::vector<MYLIB::JsonObject>& MYLIB::Json::getElements() { return _objects; }