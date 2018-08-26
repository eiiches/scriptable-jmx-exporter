def as_number_or_empty: if type == "number" then . elif type == "boolean" then (if . then 1 else 0 end) else empty end;
def array_to_entry: foreach .[] as $item (0; . + 1; {value:$item, key:.});
def map_keys(fn): to_entries | map({key: (.key | fn), value}) | from_entries;
def remap_keys($mapping): map_keys($mapping[.] // .);

def unfold_v1($labels; $name; $value; label_name_fn):
    if $value | type == "object" then
        if $value["$type"] == "javax.management.openmbean.CompositeData" then
            $value | del(."$type")
            | to_entries[] as {key: $k, value: $v}
            | unfold_v1($labels; $name + [$k]; $v; label_name_fn)
        elif $value["$type"] == "javax.management.openmbean.TabularData" then
            $value.tabular_type.index_names as $index_names
            | $value.values[]
            | $labels + ([$index_names[] as $index_name | {([$name[], $index_name]|label_name_fn): (.[$index_name]|tostring)}] | add) as $new_labels
            | del(.[$index_names[]]) | to_entries[] as {key: $k, value: $v}
            | unfold_v1($new_labels; $name + [$k]; $v; label_name_fn)
        else
            empty
        end
    elif $value | type == "array" then
        $value | array_to_entry as {key: $k, value: $v}
        | unfold_v1($labels + {([$name[], null]|label_name_fn): ($k|tostring)}; $name + [null]; $v; label_name_fn)
    else
        {labels: $labels, name: $name, value: ($value | as_number_or_empty)}
    end
;
def unfold_v1($value; label_name_fn): unfold_v1({}; []; $value; label_name_fn);

def default_transform_v1($name_keys; $attribute_as_name):
    .domain as $domain
    | .properties as $properties
    | .attribute as $attribute
    | unfold_v1(.value; map(.//"index")|join("_"))
	| .labels + $properties as $properties
	| .name as $name
	| ([$attribute, $name[] | values] | join("_")) as $attribute_name
	| {
        name: ([$domain, $properties[$name_keys[]], (if $attribute_as_name then $attribute_name else empty end)] | join(":")),
        labels: (($properties | del(.[$name_keys[]])) + (if $attribute_as_name then {} else {attribute: $attribute_name} end)),
        value
    }
;
def default_transform_v1: default_transform_v1([]; false);
def default_transform_v1($name_keys; $attribute_as_name; $label_remapping):
	default_transform_v1($name_keys; $attribute_as_name)
	| {
		labels: (.labels | remap_keys($label_remapping)),
		name,
		value
	}
;
